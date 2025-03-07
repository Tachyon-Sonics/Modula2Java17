package ch.pitchtech.modula.converter.generator.statement;

import java.util.Map;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.RepeatLoop;


public class RepeatLoopGenerator extends Generator {
    
    private final static Map<String, String> INVERSION_MAP = Map.of(
            // Key and value are both Modula-2
            "=", "<>",
            "<>", "=",
            "#", "=", // # and <> are equivalent
            "<", ">=",
            "<=", ">",
            ">", "<=",
            ">=", "<");
        
    private final RepeatLoop repeatLoop;

    
    public RepeatLoopGenerator(IHasScope scopeUnit, RepeatLoop repeatLoop) {
        super(scopeUnit, repeatLoop);
        this.repeatLoop = repeatLoop;
    }

    @Override
    public void generate(ResultContext result) {
        result.writeLine("do {");
        result.incIndent();
        for (IStatement statement : repeatLoop.getStatements())
            Statements.getGenerator(scopeUnit, statement).generate(result);
        result.decIndent();
        
        IExpression repeatCondition = repeatLoop.getCondition();
        while (repeatCondition instanceof ParenthesedExpression parenthesedExpression)
            repeatCondition = parenthesedExpression.getTarget();

        if (repeatCondition instanceof FunctionCall functionCall && functionCall.getFunctionName().equals("NOT")) {
            // Simplify double negation
            ResultContext condition = result.subContext();
            Expressions.getGenerator(scopeUnit, functionCall.getArguments().get(0)).generate(condition);
            result.writeLine("} while (" + condition.toString() + ");");
        } else if (repeatCondition instanceof InfixOpExpression infixOpExpression 
                && INVERSION_MAP.containsKey(infixOpExpression.getOperator())) {
            // Invert simple infix comparison
            ResultContext condition = result.subContext();
            InfixOpExpression reverseExpression = new InfixOpExpression(
                    infixOpExpression.getSourceLocation(),
                    infixOpExpression.getLeft(),
                    INVERSION_MAP.get(infixOpExpression.getOperator()),
                    infixOpExpression.getRight());
            Expressions.getGenerator(scopeUnit, reverseExpression).generate(condition);
            result.writeLine("} while (" + condition.toString() + ");");
        } else {
            // General case
            ResultContext condition = result.subContext();
            Expressions.getGenerator(scopeUnit, repeatCondition).generate(condition);
            result.writeIndent();
            result.write("} while (!");
            if (repeatCondition.isComplex(result))
                result.write("(");
            result.write(condition);
            if (repeatCondition.isComplex(result))
                result.write(")");
            result.write(");");
            result.writeLn();
        }
    }

}
