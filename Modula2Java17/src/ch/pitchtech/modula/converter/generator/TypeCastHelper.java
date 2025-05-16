package ch.pitchtech.modula.converter.generator;

import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.expression.ArrayAccessGenerator;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.expression.IdentifierGenerator;
import ch.pitchtech.modula.converter.generator.expression.SizeCalculator;
import ch.pitchtech.modula.converter.generator.expression.StringLiteralGenerator;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.statement.WithStatementGenerator;
import ch.pitchtech.modula.converter.generator.type.ArrayTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.RecordTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.FieldAccess;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.WithStatement;
import ch.pitchtech.modula.converter.model.type.AdrPointerType;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.utils.StringUtils;
import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class TypeCastHelper {
    
    private final ResultContext resultContext;
    
    
    public TypeCastHelper(ResultContext resultContext) {
        this.resultContext = resultContext;
    }

    /**
     * Get any required Java type cast necessary to convert from valueType to targetType
     * @param targetBoxed whether the target type is the boxed version ({@link Integer}, {@link Long}, etc) of a Java primitive type.
     * This occurs for instance when the target is a {@link IRef}.
     * @param constantAssignment whether assigning a literal constant. In Java some type casts can be dropped
     * @param varArgument whether the cast is for a VAR parameter
     * @param preValueContext filled by this method with any java type casting code to place before the value
     * @param postValueContext filled by this method with any java type casting code to place after the value
     * @return null if preValueContext and postValueContext can be used. Else, a context that replaces the whole expression
     */
    public ResultContext getRequiredTypeCast(IType targetType, boolean targetBoxed, IType valueType, IExpression value,
            boolean constantAssignment, boolean varArgument, ResultContext preValueContext, ResultContext postValueContext,
            IHasScope scopeUnit) {
        ResultContext result = null;

        result = handleNumericDownCast(targetType, targetBoxed, valueType, value, constantAssignment, preValueContext, scopeUnit);
        if (result != null)
            return result;
        
        // TODO X most of this stuff should be handled by BuiltInFunctionCallGenerator.generateAdr
        result = handleAddressToPointerCast(targetType, valueType, value, preValueContext, postValueContext, scopeUnit);
        if (result != null)
            return result;

        result = handlePointerToPointerCast(targetType, valueType, value, preValueContext, postValueContext, scopeUnit);
        if (result != null)
            return result;
        
        result = handleAdrToPointerCast(targetType, valueType, value, preValueContext, postValueContext, scopeUnit);
        if (result != null)
            return result;
        
        result = handleCastToArrayOfBytes(targetType, valueType, value, varArgument, preValueContext, postValueContext, 
                scopeUnit, value);
        if (result != null)
            return result;
        
        return null; // preValueContext and postValueContext have been filled. Continue without supplying a full replacement
    }

    private ResultContext handleNumericDownCast(IType targetType, boolean targetBoxed, IType valueType, IExpression value,
            boolean constantAssignment, ResultContext preValueContext, IHasScope scopeUnit) {
        if (targetType instanceof LiteralType literalTargetType && valueType instanceof LiteralType literalValueType) {
            if (literalTargetType.isBuiltIn() && literalValueType.isBuiltIn()) {
                // Check if value is boxed
                boolean valueBoxed = false;
                VariableDefinition byRefVariable = TypeHelper.getVariableIfByRef(value, scopeUnit, preValueContext);
                if (byRefVariable != null)
                    valueBoxed = true;
                // TODO also if value is a dereference to a simple type. Maybe the fact that a value is a boxed primitive must be moved to IExpression
                
                BuiltInType btTarget = BuiltInType.valueOf(literalTargetType.getName());
                BuiltInType btValue = BuiltInType.valueOf(literalValueType.getName());
                if (!constantAssignment) {
                    int targetSize = btTarget.getJavaSize();
                    int valueSize = btValue.getJavaSize();
                    
//                    Integer I, J = 0;
//                    int i, j = 0;
//                    Short S = 0;
//                    short s = 0;
//                    Long L = 0L;
//                    long l = 0;
//                    
//                    i = j;
//                    I = J;
//                    
//                    i = s;
//                    I = (int) s;
//                    
//                    i = S;
//                    I = (int) S;
//                    
//                    i = (int) l;
//                    I = (int) l;
//                    
//                    i = (int) (long) L;
//                    I = (int) (long) L;

                    boolean needTargetCast = (targetSize < valueSize || (targetBoxed && (targetSize != valueSize))); // (int)
                    boolean needValueCast = (valueBoxed && targetSize < valueSize); // (long)
                    if (needTargetCast) {
                        preValueContext.write("(" + btTarget.getJavaType() + ") ");
                    }
                    if (needValueCast) {
                        preValueContext.write("(" + btValue.getJavaType() + ") ");
                    }
                }
                
                if (btTarget == BuiltInType.CHAR && btValue == BuiltInType.STRING) {
                    if (value instanceof StringLiteral stringLiteral) {
                        // String of length 1 can be used as a CHAR:
                        String text = stringLiteral.getText();
                        if (text.length() == 1) {
                            ResultContext result = resultContext.subContext();
                            new StringLiteralGenerator(scopeUnit, stringLiteral).generateAsChar(result);
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private ResultContext handleAddressToPointerCast(IType targetType, IType valueType, IExpression value,
            ResultContext preValueContext, ResultContext postValueContext, IHasScope scopeUnit) {
        if (targetType instanceof PointerType targetPointerType && valueType instanceof LiteralType valueLiteralType) {
            BuiltInType btValue = BuiltInType.valueOf(valueLiteralType.getName());
            if (btValue.equals(BuiltInType.ADDRESS)) {
                // Check for NIL constant
                if (value instanceof Identifier identifier && identifier.getName().equals("NIL"))
                    return null; // null can be assigned to any Object
                
                // ADDRESS expression (mapped to Object in Java) -> cast to target Java type
                IType derefType = resultContext.resolveType(targetPointerType.getTargetType());
                if (derefType instanceof RecordType recordType) {
                    ResultContext castContext = resultContext.subContext();
                    new RecordTypeGenerator(scopeUnit, recordType).generate(castContext);
                    preValueContext.write("(" + castContext.toString() + ") ");
                } else if (derefType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                    BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                    if (biType == BuiltInType.CHAR && resultContext.getCompilerOptions().isConvertArrayOfCharToString())
                        biType = BuiltInType.STRING;
                    preValueContext.ensureJavaImport(Runtime.class);
                    preValueContext.write("Runtime.castToRef(");
                    postValueContext.write(", " + biType.getBoxedType() + ".class)");
                } else if (derefType instanceof ArrayType arrayType) {
                    ResultContext castContext = resultContext.subContext();
                    new ArrayTypeGenerator(scopeUnit, arrayType).generate(castContext);
                    preValueContext.write("(" + castContext.toString() + ") ");
                }
            }
        }
        return null;
    }

    private ResultContext handlePointerToPointerCast(IType targetType, IType valueType, IExpression value,
            ResultContext preValueContext, ResultContext postValueContext, IHasScope scopeUnit) {
        // POINTER to POINTER
        if (targetType instanceof PointerType targetPointerType 
                && valueType instanceof PointerType valuePointerType
                && valuePointerType instanceof AdrPointerType adrPointerType) {
            FunctionCall functionCall = (FunctionCall) value; // Implied by 'valuePointerType instanceof AdrPointerType'
            assert (functionCall.getFunctionName().equals("ADR")); // Implied by 'valuePointerType instanceof AdrPointerType'
            IExpression addressedExpr = functionCall.getArguments().get(0);

            // pointer := ADR(...)
            IType derefType = resultContext.resolveType(targetPointerType.getTargetType());
            if (derefType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                // pointer := ADR(expr), where expr is of a built-in (simple) type, like INTEGER
                ResultContext result = handleAddressOfValueType(preValueContext, postValueContext, scopeUnit, literalType,
                        addressedExpr);
                if (result != null)
                    return result;
            } else if (adrPointerType.getTargetType() instanceof ArrayType arrayType) {
                // pointer := ADR(array)
                IType elementType = resultContext.resolveType(arrayType.getElementType());
                if (elementType instanceof RecordType) {
                    // ADR(array) -> array[0]
                    postValueContext.write("[0]");
                }
            }
        }
        
        return null;
    }
    
    public ResultContext handleAdrToPointerCast(IType targetType, IType valueType, IExpression value, 
            ResultContext preValueContext, ResultContext postValueContext, IHasScope scopeUnit) {
        boolean isValueAdr = (value instanceof FunctionCall functionCall && functionCall.isBuiltIn(BuiltInProcedure.ADR));
        if (!isValueAdr)
            return null;
        
        if (targetType instanceof PointerType pointerType 
                && TypeHelper.isCharArrayAsString(pointerType.getTargetType(), scopeUnit.getScope(), resultContext.getCompilerOptions()))
            return null; // ADR(String) -> String
        
        if (targetType.isBuiltInType(BuiltInType.ADDRESS) 
                || targetType instanceof PointerType
                || targetType instanceof OpaqueType) {
            FunctionCall functionCall = (FunctionCall) value; 
            IExpression addressedExpr = functionCall.getArguments().get(0);
            IType addressedType = preValueContext.resolveType(addressedExpr);

            // item := ADR(...)
            if (TypeHelper.isByValueType(addressedType, scopeUnit.getScope(), resultContext.getCompilerOptions())) {
                // item := ADR(expr), where expr is of a built-in (simple) type, like INTEGER
                ResultContext result = handleAddressOfValueType(preValueContext, postValueContext, scopeUnit, addressedType,
                        addressedExpr);
                if (result != null)
                    return result;
            } else if (TypeHelper.isOpenArrayOfBytes(addressedType, scopeUnit.getScope())) {
                /*
                 * TODO X Review:
                 * - ARRAY OF BYTE should be included to TypeHelper.isByValueType, and hence this block should be handled by the previous one
                 * - This sucks: we only handle if a variable
                 */
                VariableDefinition variableDefinition = null;
                if (addressedExpr instanceof Identifier identifier) {
                    variableDefinition = scopeUnit.getScope().resolveVariable(identifier.getName());
                }
                if (variableDefinition != null && variableDefinition.isUseRef()) {
                    ResultContext result = resultContext.subContext();
                    variableDefinition.asReference(() -> {
                        Expressions.getGenerator(scopeUnit, addressedExpr).generate(result);
                    });
                    return result;
                }
            }
        }
        
        return null;
    }

    private ResultContext handleAddressOfValueType(ResultContext preValueContext, ResultContext postValueContext, IHasScope scopeUnit, 
            IType addressedType, IExpression addressedExpr) {
        VariableDefinition targetDefinition = null;
        if (addressedExpr instanceof Identifier adrIdentifier)
            targetDefinition = resultContext.getScope().resolveVariable(adrIdentifier.getName());
        if (targetDefinition != null) {
            // Quick sanity check
            if (targetDefinition.getParent() instanceof ProcedureImplementation) {
                // VariableDefinition is declared as "String toto" but is passed to an argument IRef<String> toto
                // -> The VariableDefinition should have been declared as IRef<String>
                if (!targetDefinition.isUseRef()) {
                    throw new CompilerException(addressedExpr, "ADR'ed variable {0} should have been converted to a ref", targetDefinition);
                }
            }
        }
        
        /*
         * TODO this does not look right:
         *  ADR(xx.yy) -> targetDefinition != null
         *  ADR(call()^.xx.yy) -> targetDefinition == null, but might be the same as above
         */
        if (targetDefinition == null || targetDefinition.getParent() instanceof ProcedureImplementation) {
            if (addressedType instanceof LiteralType literalType && literalType.getName().equals(BuiltInType.CHAR.name())) {
                preValueContext.ensureJavaImport(Runtime.class);
                if (resultContext.getCompilerOptions().isConvertArrayOfCharToString()) {
                    // ADR(ARRAY OF CHAR) with ARRAY OF CHAR to String conversion
                    if (targetDefinition != null && targetDefinition.isUseRef()) {
                        // ADR(Ref<String>) -> just use the Ref itself
                        ResultContext result = resultContext.subContext();
                        targetDefinition.asReference(() -> {
                            Expressions.getGenerator(scopeUnit, addressedExpr).generate(result);
                        });
                        return result;
                    } else {
                        preValueContext.write("Runtime.castToRef(");
                        postValueContext.write(", String.class)");
                        return null;
                    }
                } else {
                    BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                    preValueContext.write("Runtime.castToRef(");
                    postValueContext.write(", " + biType.getBoxedType() + ".class)");
                    return null;
                }
            }
        }
        ResultContext result = getValueTypeReference(scopeUnit, addressedExpr);
        if (result != null)
            return result;
        return null;
    }
    
    public ResultContext getValueTypeReference(IHasScope scopeUnit, IExpression addressedExpr) {
        if (addressedExpr instanceof ArrayAccess arrayAccess) {
            // Use an ArrayElementRef
            ResultContext result = resultContext.subContext();
            result.ensureJavaImport(Runtime.class);
            result.write("new Runtime.ArrayElementRef<>(");
            Expressions.getGenerator(scopeUnit, arrayAccess.getArray()).generate(result);
            IArrayType arrayType = (IArrayType) result.resolveType(arrayAccess.getArray());
            List<IExpression> headIndexes = arrayAccess.getIndexes().subList(0, arrayAccess.getIndexes().size() - 1);
            arrayType = ArrayAccessGenerator.writeArrayIndexes(scopeUnit, arrayType, headIndexes, result);
            result.write(", ");
            ResultContext lowerContext = ArrayAccessGenerator.generateLowerBound(scopeUnit, arrayType, result);
            IExpression lastIndex = arrayAccess.getIndexes().get(arrayAccess.getIndexes().size() - 1);
            ArrayAccessGenerator.writeArrayIndex(scopeUnit, lowerContext, result, lastIndex);
            result.write(")");
            return result;
        } else if (addressedExpr instanceof Identifier identifier) {
            ICompilationUnit currentCompilationUnit = scopeUnit.getCompilationUnit();
            IDefinition definition = resultContext.getScope().resolve(identifier.getName(), true, false, true, false);
            if (definition == null) {
                throw new CompilationException(identifier, "Cannot resolve '{0}'", identifier.getName());
            }
            if (definition instanceof ConstantDefinition) {
                /*
                 * ADR(constant) -> just wrap the constant in a new Runtime.Ref.
                 * Note that writing at the resulting address is not forbidden by Modula-2 but should not be done
                 * as it would change the value of a CONSTANT.
                 * 
                 * With the generated Java code it will only "write" inside the Runtime.Ref, without affecting 
                 * the actual constant.
                 * 
                 * Returning the resulting address from a PROCEDURE is not forbidden by Modula-2, but if the addressed
                 * item is a PROCEDURE local variable, it would refer to an invalid stack element and be dangerous.
                 * 
                 * With the generated Java code, it will read/write the created Runtime.Ref instead, until the reference
                 * is no longer used and can be garbage collected.
                 */
                ResultContext result = resultContext.subContext();
                result.ensureJavaImport(Runtime.class);
                result.write("new Runtime.Ref<>(");
                new IdentifierGenerator(scopeUnit, currentCompilationUnit, identifier).generate(result);
                result.write(")");
                return result;
            } else if (definition instanceof VariableDefinition variableDefinition) {
                IHasScope hasScope = variableDefinition.getParent();
                if (hasScope instanceof ICompilationUnit compilationUnit) {
                    ResultContext result = resultContext.subContext();
                    result.ensureJavaImport(Runtime.class);
                    result.write("new Runtime.FieldRef<>(");
                    String instance;
                    if (compilationUnit.getName().equals(currentCompilationUnit.getName())) {
                        instance = "this";
                    } else {
                        instance = StringUtils.toCamelCase(compilationUnit.getName());
                    }
                    result.write(instance);
                    result.write("::");
                    result.write(VariableDefinitionGenerator.getterName(result, variableDefinition));
                    result.write(", ");
                    result.write(instance);
                    result.write("::");
                    result.write(VariableDefinitionGenerator.setterName(result, variableDefinition));
                    result.write(")");
                    return result;
                } else if (hasScope instanceof ProcedureImplementation) {
                    if (!variableDefinition.isUseRef()) {
                        throw new CompilerException(addressedExpr, "PROCEDURE variable should be an IRef. PROCEDURE {0}, variable: {1}",
                                hasScope, variableDefinition);
                    }
                    // Nothing to do, variable is already an IRef
                } else if (hasScope instanceof WithStatement withStatement) {
                    if (WithStatementGenerator.isSimple(withStatement)) {
                        ResultContext exprResult = resultContext.subContext();
                        Expressions.getGenerator(scopeUnit, withStatement.getExpression()).generate(exprResult);
    
                        ResultContext result = resultContext.subContext();
                        result.ensureJavaImport(Runtime.class);
                        result.write("new Runtime.FieldRef<>(");
                        result.write(exprResult);
                        result.write("::");
                        result.write(VariableDefinitionGenerator.getterName(result, variableDefinition));
                        result.write(", ");
                        result.write(exprResult);
                        result.write("::");
                        result.write(VariableDefinitionGenerator.setterName(result, variableDefinition));
                        result.write(")");
                        return result;
                    } else {
                        // If the WITH expression is complex, be sure to use the generated variable so that the WITH expression is not reevaluated
                        ResultContext result = resultContext.subContext();
                        result.ensureJavaImport(Runtime.class);
                        result.write("new Runtime.FieldRef<>(");
                        result.write(withStatement.getLocalVariableName());
                        result.write("::");
                        result.write(VariableDefinitionGenerator.getterName(result, variableDefinition));
                        result.write(", ");
                        result.write(withStatement.getLocalVariableName());
                        result.write("::");
                        result.write(VariableDefinitionGenerator.setterName(result, variableDefinition));
                        result.write(")");
                        return result;
                    }
                }
            }
        } else if (addressedExpr instanceof FieldAccess fieldAccess) {
            // Let's say fieldAccess is 'xx^.GetMyRecord().number'
            
            // Generate expression before field access, like 'xx^.GetMyRecord()'
            ResultContext exprResult = resultContext.subContext();
            Expressions.getGenerator(scopeUnit, fieldAccess.getExpression()).generate(exprResult);
            
            // Get type before field access, like 'MyRecord'
            IType exprType = resultContext.resolveType(fieldAccess.getExpression());
            ResultContext typeResult = resultContext.subContext();
            Types.getGenerator(scopeUnit, exprType).generate(typeResult);
            
            // Generate using Runtime.FieldExprRef
            IType type = resultContext.resolveType(fieldAccess);
            ResultContext result = resultContext.subContext();
            result.ensureJavaImport(Runtime.class);
            result.write("new Runtime.FieldExprRef<>(");
            result.write(exprResult);
            result.write(", ");
            result.write(typeResult);
            result.write("::");
            result.write(VariableDefinitionGenerator.getterName(fieldAccess.getField().getName(), type));
            result.write(", ");
            result.write(typeResult);
            result.write("::");
            result.write(VariableDefinitionGenerator.setterName(fieldAccess.getField().getName(), type));
            result.write(")");
            return result;
        }
        return null;
    }
    
    /**
     * @param varParameter whether to cast for a VAR argument
     */
    private ResultContext handleCastToArrayOfBytes(IType targetType, IType valueType, IExpression value, boolean varParameter,
            ResultContext preValueContext, ResultContext postValueContext, IHasScope scopeUnit, Object sourceElement) {
        // Anything to "ARRAY OF BYTE" 
        // TODO we should probably use IRef<?> instead of byte[] after VAR promotion
        if (targetType instanceof OpenArrayType openArrayType) {
            IType elementType = resultContext.resolveType(openArrayType.getElementType());
            if (elementType.isBuiltInType(BuiltInType.BYTE)) {
                int m2Size = SizeCalculator.getModulaSizeOf(sourceElement, resultContext, valueType);
                if (varParameter) {
                    VariableDefinition variableDefinition = null;
                    if (value instanceof Identifier identifier) {
                        variableDefinition = scopeUnit.getScope().resolveVariable(identifier.getName());
                    }
                    if (variableDefinition != null) {
                        // we should be in an variableDefinition.asRef(...) block already
                        resultContext.ensureJavaImport(Runtime.class);
                        ResultContext result = resultContext.subContext();
                        result.write("Runtime.asByteArray("); // XX get value's type size
                        Expressions.getGenerator(scopeUnit, value).generate(result);
                        result.write(" /* , " + m2Size + "*/");
                        result.write(")");
                        return result;
                    } else { // TODO X try using only this version, it is more generic and should also work (refactor as necessary)
                        ResultContext result = resultContext.subContext();
                        result.write("Runtime.asByteArray("); // XX get value's type size
                        result.write(getValueTypeReference(scopeUnit, value));
                        result.write(" /* , " + m2Size + "*/");
                        result.write(")");
                        return result;
                    }
                }
                resultContext.ensureJavaImport(Runtime.class);
                preValueContext.write("Runtime.toByteArray(");
                postValueContext.write(" /* , " + m2Size + "*/");
                postValueContext.write(")");
            }
        }
        return null;
    }

}
