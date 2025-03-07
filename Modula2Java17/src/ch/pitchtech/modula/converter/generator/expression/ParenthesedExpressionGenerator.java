package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;


public class ParenthesedExpressionGenerator extends Generator {
    
    private final ParenthesedExpression parenthesedExpression;

    
    public ParenthesedExpressionGenerator(IHasScope scopeUnit, ParenthesedExpression parenthesedExpression) {
        super(scopeUnit, parenthesedExpression);
        this.parenthesedExpression = parenthesedExpression;
    }

    @Override
    public void generate(ResultContext result) {
        result.write("(");
        Expressions.getGenerator(scopeUnit, parenthesedExpression.getTarget()).generate(result);
        result.write(")");
    }

}
