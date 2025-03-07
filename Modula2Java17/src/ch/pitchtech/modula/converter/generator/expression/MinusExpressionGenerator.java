package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;


public class MinusExpressionGenerator extends Generator {
    
    private final MinusExpression expression;

    
    public MinusExpressionGenerator(IHasScope scopeUnit, MinusExpression expression) {
        super(scopeUnit, expression);
        this.expression = expression;
    }

    @Override
    public void generate(ResultContext result) {
        ResultContext targetContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression.getTarget()).generate(targetContext);
        result.write("-");
        boolean requiresParentheses = expression.getTarget().isComplex(result);
        if (requiresParentheses)
            result.write("(");
        result.write(targetContext);
        if (requiresParentheses)
            result.write(")");
    }

}
