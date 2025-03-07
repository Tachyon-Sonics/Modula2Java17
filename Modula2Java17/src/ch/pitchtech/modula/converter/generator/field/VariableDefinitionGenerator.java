package ch.pitchtech.modula.converter.generator.field;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.StringUtils;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.type.ITypePreInitializerGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.CaseVariantType;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;

public class VariableDefinitionGenerator extends Generator {
    
    private final IHasScope scopeUnit;
    private final VariableDefinition variableDefinition;


    public VariableDefinitionGenerator(IHasScope scopeUnit, VariableDefinition variableDefinition) {
        super(scopeUnit, variableDefinition);
        this.scopeUnit = scopeUnit;
        this.variableDefinition = variableDefinition;
    }

    @Override
    public void generate(ResultContext result) {
        generate(result, false, null);
    }
    
    /**
     * @param result where to generate this {@link VariableDefinition}
     * @param forceInitialValue whether to force assigning a default value to the variable
     * @param explicitInitialValue an explicit default value to assign the variable to, or <tt>null</tt> if no explicit value is to be given.
     * If not <tt>null</tt>, 'forceInitialValue' is ignored. Only if the variable is by ref.
     */
    public void generate(ResultContext result, boolean forceInitialValue, ResultContext explicitInitialValue) {
        IType type = result.resolveType(variableDefinition.getType());
        Generator typeGenerator = Types.getGenerator(scopeUnit, type);
        if (type instanceof CaseVariantType) {
            typeGenerator.generate(result);
        } else if (variableDefinition.isUseRef()) {
            if (TypeHelper.isCharArrayAsString(type, result)) {
                result.writeIndent();
                String javaQualifiers = scopeUnit.getJavaQualifiers(null, null);
                result.write(javaQualifiers);
                result.ensureJavaImport(ch.pitchtech.modula.runtime.Runtime.class);
                result.write("Runtime.Ref<String> ");
                result.write(name(variableDefinition));
                result.write(" = new Runtime.Ref<>(\"\");");
                result.writeLn();
            } else {
                // TODO test: it may fail if we have to generate an unnamed POINTER TO XX by reference
                result.writeIndent();
                String javaQualifiers = scopeUnit.getJavaQualifiers(null, null);
                result.write(javaQualifiers);
                result.ensureJavaImport(ch.pitchtech.modula.runtime.Runtime.class);
                result.write("Runtime.Ref<");
                if (type instanceof LiteralType literalType && literalType.isBuiltIn()) {
                    BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
                    String javaBoxedType = builtInType.getBoxedType();
                    result.write(javaBoxedType);
                } else {
                    typeGenerator.generate(result);
                }
                result.write("> ");
                result.write(name(variableDefinition));
                result.write(" = new Runtime.Ref<>(");
                if (explicitInitialValue != null)
                    result.write(explicitInitialValue);
                else
                    generateInitialValue(type, result, false);
                result.write(");");
                result.writeLn();
            }
        } else {
            ResultContext before = result.subContext();
            ResultContext init = result.subContext();
            init.writeIndent();
            String javaQualifiers = scopeUnit.getJavaQualifiers(null, null);
            init.write(javaQualifiers);
            typeGenerator.generate(init);
            init.write(" ");
            init.write(name(variableDefinition));
            if (typeGenerator instanceof ITypePreInitializerGenerator preInitializerGenerator) {
                preInitializerGenerator.generateInitializer(before, init, forceInitialValue);
            } else if (forceInitialValue) {
                init.write(" = ");
                generateInitialValue(type, init, true);
            }
            init.write(";");
            if (variableDefinition.isPassedAsVarAndWritten())
                init.write(" /* WRT */");
            init.writeLn();
            
            result.write(before);
            result.write(init);
        }
    }
    
    /**
     * @param assignment whether this is an assignment in the resulting Java code (and not a method argument).
     * For assignments to a constant, some casts can be avoided.
     */
    private void generateInitialValue(IType type, ResultContext result, boolean assignment) {
        if (type instanceof LiteralType literalType) {
            if (literalType.isBuiltIn()) {
                BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                String value = switch (biType) {
                    case BYTE, WORD, SHORTINT, SHORTCARD, INTEGER, CARDINAL, LONGINT, LONGCARD, LONGWORD -> (biType.getSize() == 8 ? "0L" : "0");
                    case REAL -> "0.0f";
                    case LONGREAL -> "0.0";
                    case BOOLEAN -> "false";
                    case BITSET -> "new Runtime.RangSet(0, 15)";
                    case ADDRESS, PROC -> "null";
                    case CHAR -> "(char) 0";
                    case STRING -> "\"\"";
                };
                BuiltInType initValueType = switch(biType) {
                    case BYTE, WORD, SHORTINT, SHORTCARD, INTEGER, CARDINAL, LONGINT, LONGCARD, LONGWORD -> {
                        if (biType.getSize() == 8)
                            yield BuiltInType.forJavaSize(8);
                        else
                            yield BuiltInType.forJavaSize(4);
                    }
                    case REAL -> BuiltInType.REAL;
                    case LONGREAL -> BuiltInType.LONGREAL;
                    case BOOLEAN -> BuiltInType.BOOLEAN;
                    case BITSET -> BuiltInType.BITSET;
                    case ADDRESS, PROC -> BuiltInType.ADDRESS;
                    case CHAR -> BuiltInType.CHAR;
                    case STRING -> BuiltInType.STRING;
                };
                if (initValueType.isNumeric() && biType.isNumeric() && initValueType.getSize() > biType.getSize() && !assignment) {
                    // Add a down-cast
                    result.write("(" + biType.getJavaType() + ") ");
                }
                result.write(value);
            } else {
                throw new CompilerException(variableDefinition, "Unhandled default value for type {0}", type);
            }
        } else if (type instanceof ProcedureType || type instanceof OpaqueType || type instanceof PointerType) {
            result.write("null");
        } else if (type instanceof EnumerationType enumerationType) {
            Types.getGenerator(scopeUnit, enumerationType).generate(result);
            result.write(".");
            result.write(enumerationType.getElements().get(0));
        } else if (type instanceof ArrayType) {
            // This cannot happen because ArrayTypeGenerator implements ITypePreInitializerGenerator
            throw new CompilerException(variableDefinition, "Unexpected default value requested for type {0}", type);
        } else if (type instanceof RecordType) {
            // This cannot happen because RecordTypeGenerator implements ITypePreInitializerGenerator
            throw new CompilerException(variableDefinition, "Unexpected default value requested for type {0}", type);
        } else if (type instanceof EnumSetType enumSetType) {
            result.write("EnumSet.noneOf(");
            IType enumerationType = result.resolveType(enumSetType.getEnumerationType());
            Types.getGenerator(scopeUnit, enumerationType).generate(result);
            result.write(".class)");
        } else if (type instanceof RangeSetType) {
            // This cannot happen because RangeSetTypeGenerator implements ITypePreInitializerGenerator
            throw new CompilerException(variableDefinition, "Unexpected default value requested for type {0}", type);
        } else {
            throw new CompilerException(variableDefinition, "Unhandled default value for type {0}", type);
        }
    }
    
    public void generateCaseVariantItem(IExpression caseExpr, ResultContext result) {
        result.writeIndent();
        result.write("public ");
        IType type = result.resolveType(variableDefinition.getType());
        Generator typeGenerator = Types.getGenerator(scopeUnit, type);
        typeGenerator.generate(result);
        result.write(" ");
        result.write(variableDefinition.getName());
        if (typeGenerator instanceof ITypePreInitializerGenerator preInitializerGenerator)
            preInitializerGenerator.generateInitializer(result.subContext(), result, false);
        result.write("; // ");
        Expressions.getGenerator(scopeUnit, caseExpr).generate(result);
        result.writeLn();
    }
    
    public static String getterName(ResultContext context, VariableDefinition variableDefinition) {
        String varName = variableDefinition.getName();
        IType type = context.resolveType(variableDefinition.getType());
        return getterName(varName, type);
    }

    public static String getterName(String varName, IType type) {
        if (type instanceof LiteralType literalType) {
            if (literalType.isBuiltInType(BuiltInType.BOOLEAN)) {
                return "is" + StringUtils.toPascalCase(varName);
            }
        }
        return "get" + StringUtils.toPascalCase(varName);
    }
    
    public static String setterName(ResultContext context, VariableDefinition variableDefinition) {
        String varName = variableDefinition.getName();
        return setterName(varName, null);
    }
    
    public static String setterName(String varName, IType type) {
        return "set" + StringUtils.toPascalCase(varName);
    }

    public void generateGetterAndSetter(ResultContext result) {
        // Getter
        result.writeIndent();
        result.write("public ");
        IType type = result.resolveType(variableDefinition.getType());
        Generator typeGenerator = Types.getGenerator(scopeUnit, type);
        typeGenerator.generate(result);
        result.write(" ");
        String fieldName = name(variableDefinition);
        String getterName = getterName(result, variableDefinition);
        result.write(getterName);
        result.write("() {");
        result.writeLn();
        
        result.incIndent();
        result.writeLine("return this." + fieldName + ";");
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        
        // Setter
        result.writeIndent();
        result.write("public void ");
        String setterName = setterName(result, variableDefinition);
        result.write(setterName);
        result.write("(");
        typeGenerator.generate(result);
        result.write(" ");
        result.write(fieldName);
        result.write(") {");
        result.writeLn();
        
        result.incIndent();
        result.writeLine("this." + fieldName + " = " + fieldName + ";");
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
    }

}
