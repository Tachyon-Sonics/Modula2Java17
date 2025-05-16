package ch.pitchtech.modula.converter.generator.statement;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.TypeCastHelper;
import ch.pitchtech.modula.converter.generator.expression.IdentifierGenerator;
import ch.pitchtech.modula.converter.generator.expression.SizeCalculator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.transform.NestedProcedureTransform;
import ch.pitchtech.modula.converter.transform.VariableAsRefMarker;
import ch.pitchtech.modula.converter.utils.StringUtils;

/**
 * Base class for generating PROCEDURE calls, either as a {@link FunctionCall} or as a {@link ProcedureCall}
 */
public abstract class MethodCallGenerator extends Generator {
    
    private final IMethodCall method;

    
    public MethodCallGenerator(IHasScope scopeUnit, IMethodCall method) {
        super(scopeUnit, (SourceElement) method);
        this.method = method;
    }

    /**
     * @return whether the work was delegated to {@link #handleBuiltInProcedure(BuiltInProcedure, ResultContext)}
     */
    public boolean generate(String name, List<IExpression> methodArguments, ResultContext result) {
        IDefinition definition = result.getScope().resolve(name, false, false, true, true);
        if (definition instanceof VariableDefinition procVariable) {
            IType procType = result.resolveProcedureType(procVariable.getType(), scopeUnit);
            if (procType instanceof ProcedureType procedureType) {
                generateProcedureVariableCall(procVariable, methodArguments, procedureType, result);
                return false;
            }
        }
        
        if (!(definition instanceof ProcedureDefinition procedureDefinition))
            throw new CompilationException(method, "Cannot resolve procedure: " + name);
        if (procedureDefinition.isBuiltIn()) {
            BuiltInProcedure builtInProc = BuiltInProcedure.valueOf(procedureDefinition.getName());
            handleBuiltInProcedure(builtInProc, result);
            return true;
        } else {
            String methodName = name;
            ProcedureImplementation parentProcedure = null;
            ProcedureImplementation nestedProcedure = null;
            if (procedureDefinition.getParent() != null) {
                if (procedureDefinition.getParent() instanceof ProcedureImplementation parentProcedure0) {
                    // PROCEDURE nested in another PROCEDURE
                    parentProcedure = parentProcedure0;
                    methodName = parentProcedure.getFullName() + "_" + methodName;
                    nestedProcedure = parentProcedure.getProcedures().stream().filter((p) -> p.getName().equals(name)).findFirst().orElse(null);
                    if (nestedProcedure == null)
                        throw new CompilationException(method, "Cannot find {0} in {1}", methodName, parentProcedure);
                } else {
                    qualifyInstanceIfNecessary(procedureDefinition.getParent(), result);
                }
            }
            if (procedureDefinition.getArguments().size() != methodArguments.size() && parentProcedure == null)
                throw new CompilationException(method, "The number of arguments of '{0}' does not match. Expected {1}, found {2}.",
                        procedureDefinition.getName(),
                        procedureDefinition.getArguments().size(), 
                        methodArguments.size());
            
            result.write(methodName);
            result.write("(");
            
            // Arguments
            List<String> arguments = new ArrayList<>();
            for (int i = 0; i < methodArguments.size(); i++) {
                IExpression argument = methodArguments.get(i);
                ResultContext argContext = result.subContext();

                FormalArgument formalArgument = procedureDefinition.getArguments().get(i);
                writeActualArgument(argContext, argument, formalArgument.isUseRef(), formalArgument.getType(), 
                        formalArgument.isVar());
                arguments.add(argContext.toString());
            }
            int argIndex = methodArguments.size();
            if (parentProcedure != null) {
                assert nestedProcedure != null;
                Set<String> variableNames = nestedProcedure.getVariableNames();
                Set<String> accessedNames = nestedProcedure.getAccessedNames();
                
                // add parent's arguments
                for (FormalArgument arg : parentProcedure.getArguments()) {
                    if (!variableNames.contains(arg.getName()) && accessed(accessedNames, arg.getName())) {
                        ResultContext argContext = result.subContext();
                        FormalArgument formalArgument = procedureDefinition.getArguments().get(argIndex);
                        IExpression argExpression = new Identifier(arg.getSourceLocation(), parentProcedure, arg.getName());
                        writeActualArgument(argContext, argExpression, formalArgument.isUseRef(), formalArgument.getType(),
                                formalArgument.isVar());
                        arguments.add(argContext.toString());
                        argIndex++;
                    }
                }
                // add parent's constants
                for (ConstantDefinition constant : parentProcedure.getConstantDefinitions()) {
                    if (!variableNames.contains(constant.getName()) && accessed(accessedNames, constant.getName())) {
                        ResultContext argContext = result.subContext();
                        FormalArgument formalArgument = procedureDefinition.getArguments().get(argIndex);
                        IExpression constExpression = new Identifier(constant.getSourceLocation(), parentProcedure, constant.getName());
                        writeActualArgument(argContext, constExpression, formalArgument.isUseRef(), formalArgument.getType(),
                                formalArgument.isVar());
                        arguments.add(argContext.toString());
                        argIndex++;
                    }
                }
                // add parent's variables
                for (VariableDefinition variable : parentProcedure.getVariableDefinitions()) {
                    if (!variableNames.contains(variable.getName()) && accessed(accessedNames, variable.getName())) {
                        ResultContext argContext = result.subContext();
                        FormalArgument formalArgument = procedureDefinition.getArguments().get(argIndex);
                        IExpression variableExpression = new Identifier(variable.getSourceLocation(), parentProcedure, variable.getName());
                        writeActualArgument(argContext, variableExpression, formalArgument.isUseRef(), formalArgument.getType(),
                                formalArgument.isVar());
                        arguments.add(argContext.toString());
                        argIndex++;
                    }
                }
            }
            result.write(StringUtils.toString(arguments));
            result.write(")");
            return false;
        }
    }
    
    protected abstract void handleBuiltInProcedure(BuiltInProcedure builtInProc, ResultContext result);

    protected void generateProcedureVariableCall(VariableDefinition procVariable, List<IExpression> arguments, ProcedureType procedureType, ResultContext result) {
        if (arguments.size() != procedureType.getArgumentTypes().size())
            throw new CompilationException(method, "Wrong argument count, expected " 
                    + procedureType.getArgumentTypes().size() 
                    + ", found " + arguments.size());
//        for (int i = 0; i < functionCall.getArguments().size(); i++) {
//            IType expectedType = result.resolveType(procedureType.getArgumentTypes().get(i));
//            IType actualType = result.resolveType(functionCall.getArguments().get(i).getType(result.getScope()));
//            if (!expectedType.equals(actualType))
//                throw new CompilationException(functionCall, "Argument {0} type mismatch, expected {1}, found {2}",
//                        i, expectedType, actualType);
//        }
        
        Identifier procVarIdentifier = new Identifier(procVariable.getSourceLocation(), procVariable.getParent(), procVariable.getName());
        new IdentifierGenerator(scopeUnit, scopeUnit.getCompilationUnit(), procVarIdentifier).generate(result);
        IType procVarType = result.resolveType(procVarIdentifier.getType(scopeUnit.getScope()));
        if (procVarType.isBuiltInType(BuiltInType.PROC)) {
            result.write(".run("); // Runnable
        } else {
            result.write(".invoke("); // @FunctionalInterface
        }
        writeActualArguments(result, arguments, procedureType.getArgumentTypes(), procedureType.getVarArguments());
        result.write(")");
    }

    private void writeActualArguments(ResultContext result, List<IExpression> arguments, 
            List<IType> formalArgumentTypes, BitSet varArguments) {
        for (int i = 0; i < arguments.size(); i++) {
            IExpression argument = arguments.get(i);
            if (i > 0)
                result.write(", ");
            
            writeActualArgument(result, argument, varArguments.get(i), formalArgumentTypes.get(i), varArguments.get(i));
        }
    }
    
    private void writeActualArgument(ResultContext result, IExpression argument, boolean useRef, IType formalArgumentType,
            boolean varArgument) {
        writeActualArgument(result, scopeUnit, argument, useRef, formalArgumentType, varArgument);
    }
    
    static void writeActualArgument(ResultContext result, IHasScope scopeUnit, IExpression argument, boolean useRef, 
            IType formalArgumentType, boolean varArgument) {
        IType formalType = result.resolveType(formalArgumentType);
        IType actualType = result.resolveType(argument.getType(result.getScope(), formalType));
        if (useRef) {
            VariableDefinition byRefVariable = TypeHelper.getVariableIfByRef(argument, scopeUnit, result);
            if (byRefVariable != null) {
                // Argument is already an IRef variable
                byRefVariable.asReference(() -> {
                    AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, formalType, argument, actualType, 
                            false, varArgument);
                });
                if (formalType.isBuiltInType(BuiltInType.ADDRESS) && !actualType.isBuiltInType(BuiltInType.ADDRESS)) {
                    // Convert POINTER reference (IRef<XX>) to ADDRESS reference (IRef<Object>)
                    result.write(".asAdrRef()");
                }
                return;
            } else {
                if (VariableAsRefMarker.ENABLE_ARG_AS_REF) {
                    // Argument is not an IRef. Create one (FieldRef, ArrayElementRef, etc)
                    TypeCastHelper typeCastHelper = new TypeCastHelper(result);
                    ResultContext referenceResult = typeCastHelper.getValueTypeReference(scopeUnit, argument);
                    if (referenceResult != null) {
                        result.write(referenceResult);
                        if (formalType.isBuiltInType(BuiltInType.ADDRESS) && !actualType.isBuiltInType(BuiltInType.ADDRESS)) {
                            // Convert POINTER reference (IRef<XX>) to ADDRESS reference (IRef<Object>)
                            result.write(".asAdrRef()"); // TODO there should be similar code if we have an assignment ADDRESS:= POINTER (with both variables by ref)
                        } else if (TypeHelper.isOpenArrayOfBytes(formalType, scopeUnit.getScope())) {
                            // Wrap an IRef<?> into an IRef<byte[]>
                            int m2Size = SizeCalculator.getModulaSizeOf(byRefVariable, result, actualType);
                            result.write(".asByteArray(" + m2Size + ")"); // XX size calculated from actualType?
                        }
                        return;
                    }
                }
            }
        }
        
        // Normal case:
        AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, formalType, argument, actualType, false, varArgument);
    }

    private static boolean accessed(Set<String> accessedNames, String name) {
        if (NestedProcedureTransform.USE_READ_WRITE_ANALYSIS)
            return accessedNames.contains(name);
        return true;
    }

}
