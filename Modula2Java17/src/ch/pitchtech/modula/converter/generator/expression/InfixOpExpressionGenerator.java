package ch.pitchtech.modula.converter.generator.expression;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.ArrayIndexHelper;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IByReferenceValueType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.runtime.Runtime;

public class InfixOpExpressionGenerator extends Generator {
    
    private final static Map<String, String> OPERATOR_MAP = Map.of(
            "=", "==",
            "#", "!=",
            "<>", "!=",
            "OR", "||",
            "AND", "&&",
            "DIV", "/",
            "MOD", "%");
    
    private final static Set<String> RELATION_OPERATORS = Set.of("=", "#", "<>");
    
    private final static Set<String> ON_ENUM_ORDINAL_OPERATORS = Set.of("<", "<=", ">", ">=");
    
    private final static Map<String, String> EUCLIDEAN_OPERATOR_MAP = Map.of(
            "DIV", "eDiv",
            "MOD", "eMod");
    
    private final static Map<String, String> ENUM_SET_OPERATOR_MAP = Map.of(
            "+", "Runtime.plusSet",
            "-", "Runtime.minusSet",
            "*", "Runtime.mulSet",
            "/", "Runtime.divSet");
    
    private final static Map<String, String> RANGE_SET_OPERATOR_MAP = Map.of(
            "+", "Runtime.RangeSet.plus",
            "-", "Runtime.RangeSet.minus",
            "*", "Runtime.RangeSet.mul",
            "/", "Runtime.RangeSet.div");
    
    private final static Map<String, String> ADDRESS_OPERATOR_MAP = Map.of(
            "+", "Runtime.plusAdr",
            "-", "Runtime.minusAdr");
    
    private final static Stack<InfixOpExpression> infixStack = new Stack<>();
    

    private final InfixOpExpression expression;

    
    public InfixOpExpressionGenerator(IHasScope scopeUnit, InfixOpExpression expression) {
        super(scopeUnit, expression);
        this.expression = expression;
    }
    
    public static boolean isInInfix() {
        return !infixStack.isEmpty();
    }
    
    public static InfixOpExpression currentInfixExpression() {
        return infixStack.peek();
    }
    
    public static boolean requiresMethod(IType leftType, IType rightType, String operator) {
        if (!RELATION_OPERATORS.contains(operator))
            return false;
        if (leftType instanceof IByReferenceValueType && rightType instanceof IByReferenceValueType)
            return true;
        return false;
    }
    
    public static boolean requiresEuclidean(ResultContext context, IExpression left, IExpression right, 
            IType leftType, IType rightType, String operator) {
        if (!context.getCompilerOptions().isEuclideanDivMod())
            return false;
        if (!EUCLIDEAN_OPERATOR_MAP.containsKey(operator))
            return false;
        if (leftType instanceof LiteralType literalLeftType && rightType instanceof LiteralType literalRightType) {
            if (literalLeftType.isBuiltIn() && literalRightType.isBuiltIn()) {
                BuiltInType leftBiType = BuiltInType.valueOf(literalLeftType.getName());
                BuiltInType rightBiType = BuiltInType.valueOf(literalRightType.getName());
                boolean leftSigned = leftBiType.isSigned();
                boolean rightSigned = rightBiType.isSigned();
                Object leftValue = left.evaluateConstant();
                if (leftValue instanceof Number number && number.longValue() >= 0)
                    leftSigned = false; // This is a non-negative constant
                Object rightValue = right.evaluateConstant();
                if (rightValue instanceof Number number && number.longValue() >= 0)
                    rightSigned = false; // This is a non-negative constant
                
                if (leftBiType.isNumeric() && rightBiType.isNumeric() 
                        && leftSigned && rightSigned
                        && !leftBiType.isDecimal() && !rightBiType.isDecimal())
                    return true;
            }
        }
        return false;
    }

    @Override
    public void generate(ResultContext result) {
        infixStack.push(expression);
        IType leftType = result.resolveType(expression.getLeft());
        IType rightType = result.resolveType(expression.getRight());
        if (expression.getOperator().equals("IN")) {
            generateInOp(result, leftType, rightType);
        } else if (leftType instanceof EnumSetType && rightType instanceof EnumSetType) {
            generateSetOp(result, true); // TODO Z handle BITSET too
        } else if (leftType instanceof RangeSetType && rightType instanceof RangeSetType) {
            generateSetOp(result, false);
        } else if (isAddress(leftType) && isAddress(rightType) && ADDRESS_OPERATOR_MAP.containsKey(expression.getOperator())) {
            generateAdrOp(result);
        } else if (requiresMethod(leftType, rightType, expression.getOperator())) {
            generateContentEqualityOp(result); // TODO test (seems not covered yet)
        } else if (requiresEuclidean(result, expression.getLeft(), expression.getRight(), leftType, rightType, expression.getOperator())) {
            generateEuclideanOp(result);
        } else if (leftType instanceof EnumerationType && rightType instanceof EnumerationType 
                && ON_ENUM_ORDINAL_OPERATORS.contains(expression.getOperator())) {
            generateEnumOrdinalOp(result);
        } else {
            generateOtherOp(result);
        }
        infixStack.pop();
    }
    
    public static boolean isAddress(IType type) {
        if (type instanceof PointerType)
            return true;
        if (type.isBuiltInType(BuiltInType.ADDRESS))
            return true;
        return false;
    }

    private void generateOtherOp(ResultContext result) {
        ResultContext leftContext = result.subContext();
        IExpression leftExpr = expression.getLeft();
        Expressions.getGenerator(scopeUnit, leftExpr).generate(leftContext);
        ResultContext rightContext = result.subContext();
        IExpression rightExpr = expression.getRight();
        Expressions.getGenerator(scopeUnit, rightExpr).generate(rightContext);
        
        // Detect stuff like: x <> "v", with x of type CHAR, and "v" of type STRING, but must be allowed as char
        IType leftType = result.resolveType(leftExpr);
        IType rightType = result.resolveType(rightExpr);
        if (leftType instanceof LiteralType leftLiteralType && rightType instanceof LiteralType rightLiteralType) {
            if (leftLiteralType.isBuiltIn() && rightLiteralType.isBuiltIn()) {
                BuiltInType biLeft = BuiltInType.valueOf(leftLiteralType.getName());
                BuiltInType biRight = BuiltInType.valueOf(rightLiteralType.getName());
                if (biLeft == BuiltInType.STRING && biRight == BuiltInType.CHAR) {
                    if (leftExpr instanceof StringLiteral leftConstant && leftConstant.getText().length() == 1) {
                        leftContext = result.subContext();
                        new StringLiteralGenerator(scopeUnit, (StringLiteral) leftExpr).generateAsChar(leftContext);
                    }
                } else if (biLeft == BuiltInType.CHAR && biRight == BuiltInType.STRING) {
                    if (rightExpr instanceof StringLiteral rightConstant && rightConstant.getText().length() == 1) {
                        rightContext = result.subContext();
                        new StringLiteralGenerator(scopeUnit, (StringLiteral) rightExpr).generateAsChar(rightContext);
                    }
                }
            }
        }
        
        result.write(leftContext);
        String operator = expression.getOperator();
        if (OPERATOR_MAP.containsKey(operator))
            operator = OPERATOR_MAP.get(operator);
        result.write(" " + operator + " ");
        result.write(rightContext);
    }
    
    private void generateContentEqualityOp(ResultContext result) {
        String operator = expression.getOperator();
        ResultContext leftContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
        ResultContext rightContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
        if (operator.equals("=")) {
            // Item equality
            result.write(leftContext);
            result.write(".equals(");
            result.write(rightContext);
            result.write(")");
        } else if (operator.equals("<>") || operator.equals("#")) {
            // Item inequality
            result.write("!");
            result.write(leftContext);
            result.write(".equals(");
            result.write(rightContext);
            result.write(")");
        }
    }
    
    private void generateEuclideanOp(ResultContext result) {
        String operator = expression.getOperator();
        ResultContext leftContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
        ResultContext rightContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
        String methodName = EUCLIDEAN_OPERATOR_MAP.get(operator);
        result.ensureJavaImport(Runtime.class);
        
        result.write("Runtime." + methodName + "(");
        result.write(leftContext);
        result.write(", ");
        result.write(rightContext);
        result.write(")");
    }
    
    private void generateEnumOrdinalOp(ResultContext result) {
        ResultContext leftContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
        ResultContext rightContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
        
        result.write(leftContext);
        result.write(".ordinal()");
        result.write(" " + expression.getOperator() + " ");
        result.write(rightContext);
        result.write(".ordinal()");
    }
    
    private void generateSetOp(ResultContext result, boolean enumSet) {
        String operator = expression.getOperator();
        ResultContext leftContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
        ResultContext rightContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
        if (operator.equals("=")) {
            // Set equality
            result.write(leftContext);
            result.write(".equals(");
            result.write(rightContext);
            result.write(")");
        } else if (operator.equals("<>") || operator.equals("#")) {
            // Set inequality
            result.write("!");
            result.write(leftContext);
            result.write(".equals(");
            result.write(rightContext);
            result.write(")");
        } else {
            // Set operator
            String methodName;
            if (enumSet) {
                if (!ENUM_SET_OPERATOR_MAP.containsKey(operator))
                    throw new CompilationException(expression, "Unsupported operator on SET type: " + operator);
                methodName = ENUM_SET_OPERATOR_MAP.get(operator);
            } else {
                if (!RANGE_SET_OPERATOR_MAP.containsKey(operator))
                    throw new CompilationException(expression, "Unsupported operator on SET type: " + operator);
                methodName = RANGE_SET_OPERATOR_MAP.get(operator);
            }
            result.ensureJavaImport(Runtime.class);
            
            result.write(methodName);
            result.write("(");
            result.write(leftContext);
            result.write(", ");
            result.write(rightContext);
            result.write(")");
        }
    }
    
    private void generateAdrOp(ResultContext result) {
        String operator = expression.getOperator();
        if (!ADDRESS_OPERATOR_MAP.containsKey(operator))
            throw new CompilationException(expression, "Unsupported operator on pointer type: " + operator);
        String methodName = ADDRESS_OPERATOR_MAP.get(operator);
        result.ensureJavaImport(Runtime.class);
        ResultContext leftContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
        ResultContext rightContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
        
        result.write(methodName);
        result.write("(");
        result.write(leftContext);
        result.write(", ");
        result.write(rightContext);
        result.write(")");
    }
    
    private void generateInOp(ResultContext result, IType leftType, IType rightType) {
        if (rightType instanceof RangeSetType) {
            ResultContext leftContext = result.subContext();
            ArrayIndexHelper.writeIntIndex(scopeUnit, expression.getLeft(), leftContext);
            ResultContext rightContext = result.subContext();
            Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
            
            result.write(rightContext);
            result.write(".contains(");
            result.write(leftContext);
            result.write(")");
        } else {
            if (!(leftType instanceof EnumerationType))
                throw new CompilationException(expression.getLeft(), "Expression of enumerated type expected. Found {0}", leftType);
            if (!(rightType instanceof EnumSetType))
                throw new CompilationException(expression.getRight(), "Expression of SET type expected. Found {0}", rightType);
            ResultContext leftContext = result.subContext();
            Expressions.getGenerator(scopeUnit, expression.getLeft()).generate(leftContext);
            ResultContext rightContext = result.subContext();
            Expressions.getGenerator(scopeUnit, expression.getRight()).generate(rightContext);
            
            result.write(rightContext);
            result.write(".contains(");
            result.write(leftContext);
            result.write(")");
        }
    }
    
}
