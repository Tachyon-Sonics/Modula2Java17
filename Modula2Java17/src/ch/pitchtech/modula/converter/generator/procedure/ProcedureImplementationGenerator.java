package ch.pitchtech.modula.converter.generator.procedure;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.field.ConstantDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.statement.Statements;
import ch.pitchtech.modula.converter.generator.type.ProcedureTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;

// TODO idea: optionally generate javadoc with original Modula-2 declaration + conversion comments: (VAR not written, not VAR but used as VAR, not VAR, needs copy)
public class ProcedureImplementationGenerator extends Generator {
    
    private final ProcedureImplementation procedureImplementation;

    
    public ProcedureImplementationGenerator(IHasScope scopeUnit, ProcedureImplementation procedureImplementation) {
        super(scopeUnit, procedureImplementation);
        this.procedureImplementation = procedureImplementation;
    }

    @Override
    public void generate(ResultContext result) {
        String name = procedureImplementation.getName();
        String fullName = procedureImplementation.getFullName();
        
        // Get return type
        String javaReturnType = "void";
        IType pReturnType = procedureImplementation.getReturnType();
        if (pReturnType != null) {
            IType returnType = result.resolveType(pReturnType);
            ResultContext typeContext = result.subContext();
            Types.getGenerator(scopeUnit, returnType).generate(typeContext);
            javaReturnType = typeContext.toString();
        }
        
        // Get arguments
        List<FormalArgument> argumentsToCopy = new ArrayList<>();
        List<FormalArgument> argumentsToWrapAsRef = new ArrayList<>();
        ResultContext argumentsContext = result.subContext();
        boolean isFirst = true;
        for (FormalArgument formalArgument : procedureImplementation.getArguments()) {
            if (!isFirst)
                argumentsContext.write(", ");
            isFirst = false;
            new FormalArgumentGenerator(scopeUnit, formalArgument).generate(argumentsContext, true, argumentsToCopy, argumentsToWrapAsRef);
        }
        String javaArguments = argumentsContext.toString();
        
        // Check if it exists on the corresponding definition module
        boolean isDefined = false;
        if (procedureImplementation.getParentNode() instanceof ImplementationModule implementationModule) {
            DefinitionModule definitionModule = implementationModule.getDefinition();
            for (ProcedureDefinition pd : definitionModule.getProcedureDefinitions()) {
                if (pd.getName().equals(name))
                    isDefined = true;
            }
        }
        
        // Write header
        if (isDefined) {
            result.writeLine("public " + javaReturnType + " " + fullName + "(" + javaArguments + ") {");
        } else {
            result.writeLine("private " + javaReturnType + " " + fullName + "(" + javaArguments + ") {");
        }
        result.incIndent();
        
        // Clone argument references that should be by-value
        for (FormalArgument formalArgument : argumentsToCopy) {
            IType argType = result.resolveType(formalArgument.getType());
            ResultContext typeContext = result.subContext();
            Types.getGenerator(scopeUnit, argType).generate(typeContext);

            String argFinalName = name(formalArgument);
            String argTmpName = "_" + name(formalArgument);
            
            result.writeIndent();
            result.write(typeContext);
            result.write(" " + argFinalName);
            result.write(" = ");
            if (argType instanceof RecordType) {
                result.write(argTmpName);
                result.write(".newCopy()");
            } else if (argType instanceof EnumSetType || argType instanceof RangeSetType) {
                result.write(argTmpName);
                result.write(".clone()");
            } else if (argType instanceof IArrayType arrayType) {
                IType elementType = result.resolveType(arrayType.getElementType());
                boolean deep = (!(elementType instanceof PointerType) 
                        && !(elementType instanceof OpaqueType) 
                        && !elementType.isBuiltInType(BuiltInType.ADDRESS));
                result.ensureJavaImport(Runtime.class);
                result.write("Runtime.copyOf(" + deep + ", ");
                result.write(argTmpName);
                result.write(")");
            } else {
                throw new CompilerException(formalArgument, "Unable to clone argument");
            }
            
            result.write("; // By-value and written argument");
            result.writeLn();
        }
        // Wrap non-VAR arguments that are passed as VAR argument or addressed
        for (FormalArgument formalArgument : argumentsToWrapAsRef) {
            formalArgument.setUseRef(true); // Make sure all users now use it as a reference
            
            // Create reference variable, with value variable (with "_" prefix) as initial value
            String argFinalName = name(formalArgument);
            String argTmpName = "_" + name(formalArgument);
            ResultContext argTmpContext = result.subContext();
            argTmpContext.write(argTmpName);
            
            VariableDefinition wrappedVariable = new VariableDefinition(formalArgument.getSourceLocation(), 
                    procedureImplementation, argFinalName, formalArgument.getType());
            wrappedVariable.copyAccessDataFrom(formalArgument);
            new VariableDefinitionGenerator(procedureImplementation, wrappedVariable).generate(result, false, argTmpContext);
        }
        if (!argumentsToCopy.isEmpty() || !argumentsToWrapAsRef.isEmpty())
            result.writeLn();
        
        result.pushScope(procedureImplementation.getLocalScope());
        
        // Constants
        if (!procedureImplementation.getConstantDefinitions().isEmpty()) {
            result.writeLine("// CONST");
            for (ConstantDefinition constantDefinition : procedureImplementation.getConstantDefinitions()) {
                new ConstantDefinitionGenerator(constantDefinition, procedureImplementation).generate(result);
            }
            result.writeLn();
        }
        
        // Types
        if (!procedureImplementation.getTypeDefinitions().isEmpty()) {
            ResultContext typeResult = result.subContext();
            for (TypeDefinition typeDefinition : procedureImplementation.getTypeDefinitions()) {
                ResultContext typeContext = result.subContext();
                new TypeDefinitionGenerator(procedureImplementation, typeDefinition).generate(typeContext);
                if (!typeContext.toString().isBlank()) {
                    typeResult.write(typeContext);
                    typeResult.writeLn();
                }
            }
            if (!typeResult.toString().isBlank()) {
                result.writeLine("// TYPE");
                result.write(typeResult);
            }
        }
        
        // Variables
        if (!procedureImplementation.getVariableDefinitions().isEmpty()) {
            result.writeLine("// VAR");
            for (VariableDefinition variableDefinition : procedureImplementation.getVariableDefinitions()) {
                new VariableDefinitionGenerator(procedureImplementation, variableDefinition).generate(result, true, null);
            }
            result.writeLn();
        }
        
        // Body
        for (IStatement statement : procedureImplementation.getStatements()) {
            Statements.getGenerator(procedureImplementation, statement).generate(result);
        }
        
        result.popScope();
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        
        // References, if procedure is used as an expression
        ProcedureDefinition procedureDefinition = procedureImplementation.findDefinition();
        if (procedureDefinition == null)
            procedureDefinition = procedureImplementation.asDefinition();
        if (procedureDefinition != null && !procedureDefinition.getUsedAsExprTypes().isEmpty()) {
            for (ProcedureType procedureType : procedureDefinition.getUsedAsExprTypes()) {
                result.writeIndent();
                if (isDefined) {
                    result.write("public ");
                } else {
                    result.write("private ");
                }
                result.write("final ");
                new ProcedureTypeGenerator(procedureImplementation.getCompilationUnit(), procedureType).generate(result);
                String refName = getProcedureReferenceName(this, procedureDefinition, procedureType);
                result.write(" " + refName + " = this::" + fullName + ";");
                result.writeLn();
            }
            result.writeLn();
        }
    }
    
    /**
     * When a procedure is used as an expression, and {@link CompilerOptions#isInlineProcedureAsExpression()} is false,
     * a Java final variable is generated for the procedure reference (with <tt>this::myProc</tt> as value).
     * <p>
     * This returns the name of the Java variable. Multiple variables can exist if the same procedure is used as
     * expression of different types.
     */
    public static String getProcedureReferenceName(Generator generator, ProcedureDefinition procedure, ProcedureType targetType) {
        if (procedure.getUsedAsExprTypes().size() == 1) {
            // Procedure is only used as expression of a single type. Use simpler name
            return procedure.getName() + "_ref";
        } else {
            // Procedure is used as expressions of multiple types. Use complete name
            String name = procedure.getName();
            String procName = targetType.isProc() ? "Runnable" : generator.name(targetType);
            String cuName = "";
            IHasScope scopeUnit = targetType.getDeclaringScope();
            if (scopeUnit instanceof ICompilationUnit cu) {
                cuName = "_" + cu.getName();
            }
            String refName = name + "_as" + cuName + "_" + procName;
            return refName;
        }
    }
    
}
