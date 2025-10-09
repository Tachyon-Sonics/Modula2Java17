package ch.pitchtech.modula.converter.generator;

import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class ArrayIndexHelper {
    
    /**
     * Check if the given expression is of integer type. If yes, add type casts so that it is suitable for
     * a Java array access (or any other construct requiring an <tt>int</tt>).
     * <p>
     * Basically, this will generate a downcast to <tt>int</tt> if the expression is <tt>long</tt>.
     */
    public static void castToIntIndex(IExpression indexExpr, ResultContext preIndexContext, ResultContext postIndexContext) {
        IType indexType = preIndexContext.resolveType(indexExpr);
        if (indexType instanceof LiteralType literalType && literalType.isBuiltIn()) {
            BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
            if (builtInType.getJavaType().equals("long")) {
                preIndexContext.write("(int) "); // Cast to int
                if (indexExpr.isComplex(preIndexContext)) {
                    preIndexContext.write("(");
                    postIndexContext.write(")");
                }
            }
        }
    }
    
    /**
     * Write the given index expression and downcast it to <tt>int</tt> if necessary
     */
    public static void writeIntIndex(IHasScope scopeUnit, IExpression indexExpr, ResultContext result) {
        ResultContext postIndex = result.subContext();
        castToIntIndex(indexExpr, result, postIndex);
        Expressions.getGenerator(scopeUnit, indexExpr).generate(result);
        result.write(postIndex);
    }

}
