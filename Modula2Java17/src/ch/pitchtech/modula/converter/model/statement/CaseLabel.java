package ch.pitchtech.modula.converter.model.statement;

import ch.pitchtech.modula.converter.model.expression.IExpression;

/**
 * @param expr unique expression, or lower bound in a range (<tt>e1..e2</tt>) label
 * @param toExpr upper bound in a range label
 */
public record CaseLabel(IExpression expr, IExpression toExpr) {
    
    public CaseLabel(IExpression expr) {
        this(expr, null);
    }

}
