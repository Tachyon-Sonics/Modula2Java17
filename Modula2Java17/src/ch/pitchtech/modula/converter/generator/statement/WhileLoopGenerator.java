package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.WhileLoop;


public class WhileLoopGenerator extends Generator {
    
    private final WhileLoop whileLoop;

    
    public WhileLoopGenerator(IHasScope scopeUnit, WhileLoop whileLoop) {
        super(scopeUnit, whileLoop);
        this.whileLoop = whileLoop;
    }

    @Override
    public void generate(ResultContext result) {
        IExpression whileCondition = whileLoop.getCondition();
        while (whileCondition instanceof ParenthesedExpression parenthesedExpression)
            whileCondition = parenthesedExpression.getTarget();
        
        ResultContext condition = result.subContext();
        Expressions.getGenerator(scopeUnit, whileCondition).generate(condition);
        result.writeLine("while (" + condition.toString() + ") {");
        result.incIndent();
        for (IStatement statement : whileLoop.getStatements())
            Statements.generate(scopeUnit, statement, result);
        result.decIndent();
        result.writeLine("}");
    }

}
