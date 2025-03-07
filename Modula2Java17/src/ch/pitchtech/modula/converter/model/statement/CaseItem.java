package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;

public record CaseItem(List<CaseLabel> labels, List<IStatement> statements) {
    
    public List<IExpression> getLabelExpressions() {
        List<IExpression> result = new ArrayList<>();
        for (CaseLabel label : labels) {
            result.add(label.expr());
            if (label.toExpr() != null)
                result.add(label.toExpr());
        }
        return result;
    }
    
}
