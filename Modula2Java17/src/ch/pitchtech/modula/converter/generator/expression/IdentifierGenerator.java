package ch.pitchtech.modula.converter.generator.expression;

import java.util.Map;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.procedure.ProcedureImplementationGenerator;
import ch.pitchtech.modula.converter.generator.statement.AssignmentGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ILocalVariable;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.WithStatement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.converter.utils.StringUtils;
import ch.pitchtech.modula.runtime.Runtime;

public class IdentifierGenerator extends Generator implements IAssignGenerator {
    
    private final static Map<String, String> BUILTIN_IDENTIFIERS = Map.of(
            "TRUE", "true",
            "FALSE", "false",
            "NIL", "null");
    
    
    private final Identifier identifier;
    private final IType targetType;

    
    public IdentifierGenerator(IHasScope scopeUnit, ICompilationUnit compilationUnit, Identifier identifier, IType targetType) {
        super(scopeUnit, compilationUnit, identifier);
        this.identifier = identifier;
        this.targetType = targetType;
    }

    public IdentifierGenerator(IHasScope scopeUnit, ICompilationUnit compilationUnit, Identifier identifier) {
        this(scopeUnit, compilationUnit, identifier, null);
    }
    
    @Override
    public void generate(ResultContext result) {
        String name = identifier.getName();
        if (BUILTIN_IDENTIFIERS.containsKey(name)) {
            result.write(BUILTIN_IDENTIFIERS.get(name));
            return;
        }
        
        IDefinition definition = scopeUnit.getScope().resolve(name, true, false, true, true);
        if (definition == null)
            throw new CompilationException(identifier, "Cannot resolve symbol: " + name);
        
        // Check for WITH statement scope
        if (definition instanceof VariableDefinition variableDefinition
                && variableDefinition.getParent() instanceof WithStatement withStatement) {
            result.write(withStatement.getLocalVariableName() + ".");
        }
        
        // Get declaring compilation unit
        ICompilationUnit declaringUnit = definition.getParent().getCompilationUnit();
        if (declaringUnit == null)
            throw new CompilationException(identifier, "Cannot resolve declaring unit for symbol: " + name);
        
        // Check if member of an enum
        TypeDefinition enumTypeDefinition = declaringUnit.getEnumTypeForEnumMember(name);

        // Qualify as necessary
        if (definition instanceof VariableDefinition variableDefinition) {
            qualifyInstanceIfNecessary(variableDefinition.getParent(), result);
        } else if (definition instanceof ConstantDefinition constantDefinition) {
            if (enumTypeDefinition == null) {
                qualifyStaticIfNecessary(constantDefinition.getParent(), result);
            } else {
                // Import enum type
                if (needQualifier(declaringUnit)) {
                    String targetPackage;
                    if (declaringUnit.getDefinitionModule().isImplemented())
                        targetPackage = result.getCompilerOptions().getTargetPackageMain();
                    else
                        targetPackage = result.getCompilerOptions().getTargetPackageLib();
                    result.ensureJavaImport(targetPackage + "." 
                            + declaringUnit.getDefinitionModule().getName() + "."
                            + enumTypeDefinition.getName());
                }
            }
        } else if (definition instanceof ProcedureDefinition procedureDefinition) {
            IHasScope scope = procedureDefinition.getParent();
            String moduleName = scope.getCompilationUnit().getName();
            IType castType = null;
            if (targetType == null) {
                InfixOpExpression infix = InfixOpExpressionGenerator.currentInfixExpression();
                if (infix != null) {
                    IExpression other = infix.getLeft() == identifier ? infix.getRight() : infix.getLeft();
                    castType = result.resolveType(other);
                }
            } else {
                castType = result.resolveType(targetType);
            }
            
            if (result.getCompilerOptions().isInlineProcedureAsExpression()) {
                // Use Runtime.proc(...) inline expression
                result.ensureJavaImport(Runtime.class);
                result.write("Runtime.proc(");
                if (InfixOpExpressionGenerator.isInInfix()) {
                    // Add type cast
                    if (castType != null) {
                        result.write("(");
                        Types.getGenerator(scopeUnit, castType).generate(result);
                        result.write(") ");
                    }
                }
                if (needQualifier(scope.getCompilationUnit())) {
                    result.ensureModuleInstance(moduleName);
                    result.write(StringUtils.toCamelCase(moduleName) + "::");
                } else {
                    result.write("this::");
                }
                result.write(name(identifier));
                result.write(", \"");
                result.write(moduleName);
                result.write(".");
                result.write(name);
                result.write("\")");
                return;
            } else {
                assert !result.getCompilerOptions().isInlineProcedureAsExpression();
                // Use generated procedure references
                IType forType = (targetType == null ? castType : targetType);
                ProcedureType procedureType = (ProcedureType) result.resolveProcedureType(forType, scope);
                String procedureRefName = ProcedureImplementationGenerator.getProcedureReferenceName(this, procedureDefinition, procedureType);
                if (needQualifier(scope.getCompilationUnit())) {
                    result.ensureModuleInstance(moduleName);
                    result.write(StringUtils.toCamelCase(moduleName) + ".");
                }
                result.write(procedureRefName);
                return;
            }
        }
        
        if (enumTypeDefinition != null) {
            // Enum member. Prefix with enum type:
            result.write(enumTypeDefinition.getName() + ".");
        }
        
        result.write(name(identifier));
        
        if (definition instanceof VariableDefinition variableDefinition && isUseRef(variableDefinition)) {
            result.write(".get()");
        }
    }
    
    private static boolean isUseRef(ILocalVariable variable) {
        while (variable.getSurrogateFor() != null)
            variable = variable.getSurrogateFor();
        return variable.isUseRef();
    }

    @Override
    public boolean generateAssignement(ResultContext result, IExpression value) {
        VariableDefinition variableDefinition = TypeHelper.getVariableIfByRef(identifier, scopeUnit, result);
        if (variableDefinition != null) {
            assert variableDefinition.isUseRef();
            ResultContext assignedValue = result.subContext();
            IType type = result.resolveType(identifier);
            IType valueType = result.resolveType(value);
            AssignmentGenerator.writeValueWithProperCast(assignedValue, scopeUnit, type, true, value, valueType, false, false);
            generateRefAssignement(result, assignedValue, variableDefinition);
            return true;
        }
        return false;
    }

    @Override
    public boolean generateAssignement(ResultContext result, ResultContext assignedValue) {
        VariableDefinition variableDefinition = TypeHelper.getVariableIfByRef(identifier, scopeUnit, result);
        if (variableDefinition != null) {
            assert variableDefinition.isUseRef();
            // This identifier was wrapped into a Ref. Use Ref.set(...)
            generateRefAssignement(result, assignedValue, variableDefinition);
            return true;
        }
        return false;
    }

    private void generateRefAssignement(ResultContext result, ResultContext assignedValue, VariableDefinition variableDefinition) {
        variableDefinition.asReference(() -> {
            Expressions.getGenerator(scopeUnit, identifier).generate(result);
        });
        result.write(".set(");
        result.write(assignedValue);
        result.write(")");
    }

}
