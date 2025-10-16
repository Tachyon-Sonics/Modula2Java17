package ch.pitchtech.modula.converter.generator.statement;

import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.TypeCastHelper;
import ch.pitchtech.modula.converter.generator.expression.ArrayAccessGenerator;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.expression.IAssignGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IByReferenceValueType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.runtime.Runtime;
import ch.pitchtech.modula.runtime.Runtime.IRef;


public class AssignmentGenerator extends Generator {
    
    private final Assignement assignement;

    
    public AssignmentGenerator(IHasScope scopeUnit, Assignement assignement) {
        super(scopeUnit, assignement);
        this.assignement = assignement;
    }

    @Override
    public void generate(ResultContext result) {
        ResultContext assignmentContext = result.subContext(); // For easier debugging
        generate(assignmentContext, false);
        result.write(assignmentContext);
    }

    public void generate(ResultContext result, boolean inline) {
        IExpression target = assignement.getTarget();
        IType targetType = result.resolveType(target);
        IExpression value = assignement.getValue();
        IType valueType = result.resolveType(value, targetType);
        
        if (!inline)
            result.writeIndent();
        if (target instanceof ArrayAccess arrayAccess && ArrayAccessGenerator.isStringAccess(result, arrayAccess)) {
            // "charArray[x] := value", where "charArray" has been promoted to String
            if (inline)
                throw new CompilerException(assignement, "Inline mode not supported (and not expected - reserved for FOR loops) for {0}", arrayAccess);
            generateCharArrayElementAssignment(result, targetType, value, valueType, arrayAccess);
            return;
        }
        
        try {
            Generator targetGenerator = Expressions.getGenerator(scopeUnit, target);
            if (targetGenerator instanceof IAssignGenerator assignGenerator) {
                /*
                 * The generator may have a special way of handling the assignment, such as calling
                 * a setter:
                 */
                boolean assigned = assignGenerator.generateAssignement(result, value);
                if (assigned) {
                    return;
                }
            }
            
            if (targetType instanceof IByReferenceValueType 
                    && valueType instanceof IByReferenceValueType 
                    && !value.isConstant(scopeUnit.getScope())) {
                // Copy values
                boolean closeParenthese = true;
                if (targetType instanceof EnumSetType || targetType instanceof RangeSetType) {
                    Expressions.getGenerator(scopeUnit, target).generate(result);
                    if (value instanceof InfixOpExpression) {
                        // The value is a set expression, which is already a copy
                        result.write(" = ");
                        if (value.isComplex(result)) {
                            result.write("("); // This should never be the case...
                        } else {
                            closeParenthese = false;
                        }
                    } else {
                        if (targetType instanceof EnumSetType) {
                            result.write(" = EnumSet.copyOf(");
                        } else {
                            assert targetType instanceof RangeSetType;
                            result.write(".copyFrom(");
                        }
                    }
                    writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, false, false);
                } else if (targetType instanceof IArrayType arrayType) {
                    IType elementType = result.resolveType(arrayType.getElementType());
                    boolean deep = (!(elementType instanceof PointerType) 
                            && !(elementType instanceof OpaqueType) 
                            && !elementType.isBuiltInType(BuiltInType.ADDRESS));
                    if (TypeHelper.isCharArrayAsString(targetType, result)) {
                        // Use copyOf() because we cannot mutate a String
                        Expressions.getGenerator(scopeUnit, target).generate(result);
                        result.write(" = Runtime.copyOf(" + deep + ", ");
                        writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, false, false);
                    } else {
                        // Use copyArray to mutate the target array
                        result.ensureJavaImport(Runtime.class);
                        result.write("Runtime.copyArray(" + deep + ", ");
                        Expressions.getGenerator(scopeUnit, target).generate(result);
                        result.write(", ");
                        writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, false, false);
                    }
                } else if (targetType instanceof RecordType) {
                    Expressions.getGenerator(scopeUnit, target).generate(result);
                    result.write(".copyFrom(");
                    writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, false, false);
                } else {
                    throw new CompilerException(assignement, "Not handled: {0}", targetType);
                }
                if (closeParenthese)
                    result.write(")");
                return;
            }
    
            // Normal case
            targetGenerator.generate(result);
            result.write(" = ");
            writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, true, false);
        } finally {
            if (!inline) {
                result.write(";");
                result.writeLn();
            }
        }
    }

    private void generateCharArrayElementAssignment(ResultContext result, IType targetType, IExpression value, IType valueType, ArrayAccess arrayAccess) {
        IExpression arrayExpr = arrayAccess.getArray();
        ResultContext arrayReadContext = result.subContext();
        Generator generator = Expressions.getGenerator(scopeUnit, arrayExpr);
        generator.generate(arrayReadContext);
        
        IType type = result.resolveType(arrayExpr);
        if (!(type instanceof IArrayType arrayType))
            throw new CompilationException(arrayAccess, "Not an array");
        
        // General case: a[x][y] = Runtime.setChar(a[x][y],  z, value)
        //                 head                      head   last
        
        // Head
        List<IExpression> indexes = arrayAccess.getIndexes();
        List<IExpression> headIndexes = indexes.subList(0, indexes.size() - 1);
        ArrayAccessGenerator.writeArrayIndexes(scopeUnit, arrayType, headIndexes, arrayReadContext);
        
        // Last index
        IExpression indexExpr = arrayAccess.getIndexes().get(arrayAccess.getIndexes().size() - 1);
        ResultContext lowerContext = ArrayAccessGenerator.generateLowerBound(scopeUnit, arrayType, result);
        
        // Check if we have a Ref<String>
        VariableDefinition arrayVariableByRef = TypeHelper.getVariableIfStringByRef(arrayExpr, scopeUnit, result);
        
        result.ensureJavaImport(Runtime.class);
        if (arrayVariableByRef != null) {
            // char array (a[]) is a IRef<String>. Use simplified version:
            //     Runtime.setChar(a, index, value);
            result.write("Runtime.setChar(");
            arrayVariableByRef.asReference(() -> {
                generator.generate(result);
            });
            result.write(", ");
            ArrayAccessGenerator.writeArrayIndex(scopeUnit, lowerContext, result, indexExpr);
            result.write(", ");
            writeValueWithProperCast(result, scopeUnit, targetType, value, valueType, false, false);
            result.write(");");
        } else {
            // a[x][y] = Runtime.setChar(a[x][y], z, value)
            ResultContext assignedValue = result.subContext();
            assignedValue.write("Runtime.setChar(");
            assignedValue.write(arrayReadContext);
            assignedValue.write(", ");
            ArrayAccessGenerator.writeArrayIndex(scopeUnit, lowerContext, assignedValue, indexExpr);
            assignedValue.write(", ");
            writeValueWithProperCast(assignedValue, scopeUnit, targetType, value, valueType, false, false);
            assignedValue.write(")");
            
            boolean assigned = false;
            if (generator instanceof IAssignGenerator assignGenerator) {
                assigned = assignGenerator.generateAssignement(result, assignedValue);
            }
            
            if (!assigned) {
                result.write(arrayReadContext);
                result.write(" = ");
                result.write(assignedValue);
            }
            result.write(";");
        }
        result.writeLn();
    }

    /**
     * Write a value, adding a type cast if necessary.
     * <p>
     * If a type cast is added and the value is an infix operation, the value is further surrounded by parentheses.
     * @param result where to write the value
     * @param targetType target type, used to add a type cast if it does not match the type of the value
     * @param targetBoxed whether the target type is the boxed version ({@link Integer}, {@link Long}, etc) of a Java primitive type.
     * This occurs for instance when the target is a {@link IRef}.
     * @param value the value to write
     * @param valueType type of the value
     * @param assignment whether this is an assignment in the resulting Java code (and not a method argument).
     * For assignments to a constant, some casts can be avoided.
     * @param varArgument whether this is for a VAR parameter. Implies 'assignment' to be false
     */
    public static void writeValueWithProperCast(ResultContext result, IHasScope scopeUnit,
            IType targetType, boolean targetBoxed, IExpression value, IType valueType, 
            boolean assignment, boolean varArgument) {
        
        // Check if assigning a constant
        // When assigning a constant, no need to cast (byte) or (short)
        boolean constantAssignment = (assignment && value instanceof ConstantLiteral);
        if (assignment) {
            if (value instanceof MinusExpression minusExpression 
                    && minusExpression.getTarget() instanceof ConstantLiteral)
                constantAssignment = true; // assigning to "-<literal>"
            if (value instanceof Identifier identifier) {
                ConstantDefinition constantDefinition = scopeUnit.getScope().resolveConstant(identifier.getName());
                if (constantDefinition != null)
                    constantAssignment = true; // assigning to "<constant>"
            }
            if (value instanceof MinusExpression minusExpression 
                    && minusExpression.getTarget() instanceof Identifier identifier) {
                ConstantDefinition constantDefinition = scopeUnit.getScope().resolveConstant(identifier.getName());
                if (constantDefinition != null)
                    constantAssignment = true; // assigning to "-<constant>"
            }
        }
        
        // Get required type cast; add parentheses as necessary
        ResultContext preValueContext = result.subContext();
        ResultContext postValueContext = result.subContext();
        TypeCastHelper typeCastHelper = new TypeCastHelper(result);
        ResultContext valueContext = typeCastHelper.getRequiredTypeCast(targetType, targetBoxed, 
                valueType, value, constantAssignment, varArgument,
                preValueContext, postValueContext, scopeUnit);
        if (valueContext == null) {
            boolean requiresParenteses = false;
            if (!preValueContext.toString().isEmpty() || !postValueContext.toString().isEmpty()) {
                result.write(preValueContext);
                if (value.isComplex(result))
                    requiresParenteses = true;
            }
            if (requiresParenteses)
                result.write("(");
            Expressions.getGenerator(scopeUnit, value, targetType).generate(result);
            if (requiresParenteses)
                result.write(")");
            result.write(postValueContext);
        } else {
            result.write(valueContext);
        }
    }
    
    /**
     * Write a value, adding a type cast if necessary.
     * <p>
     * If a type cast is added and the value is an infix operation, the value is further surrounded by parentheses.
     * @param result where to write the value
     * @param targetType target type, used to add a type cast if it does not match the type of the value
     * @param value the value to write
     * @param valueType type of the value
     * @param assignment whether this is an assignment in the resulting Java code (and not a method argument).
     * For assignments to a constant, some casts can be avoided.
     * @param varArgument whether this is for a VAR parameter. Implies 'assignment' to be false
     */
    public static void writeValueWithProperCast(ResultContext result, IHasScope scopeUnit,
            IType targetType, IExpression value, IType valueType, boolean assignment, boolean varArgument) {
        writeValueWithProperCast(result, scopeUnit, targetType, false, value, valueType, assignment, varArgument);
    }
    
}
