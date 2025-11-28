package ch.pitchtech.modula.converter.generator.statement;

import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.ElsIfStatement;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.IStatementsContainer;
import ch.pitchtech.modula.converter.model.statement.IfStatement;


public class IfStatementGenerator extends Generator {
    
    private final IfStatement ifStatement;

    
    public IfStatementGenerator(IHasScope scopeUnit, IfStatement ifStatement) {
        super(scopeUnit, ifStatement);
        this.ifStatement = ifStatement;
    }

    @Override
    public void generate(ResultContext result) {
        IExpression ifExpression = ifStatement.getCondition();
        while (ifExpression instanceof ParenthesedExpression parenthesedExpression)
            ifExpression = parenthesedExpression.getTarget();
        
        ResultContext ifCondition = result.subContext();
        Expressions.getGenerator(scopeUnit, ifExpression).generate(ifCondition);
        
        boolean simple = false;
        if (IStatementsContainer.SIMPLIFY_SINLE_LINE_STATEMENTS)
            simple = isSimple();
        if (simple) {
            result.writeLine("if (" + ifCondition.toString() + ")");
            generateSubStatements(ifStatement.getThenStatements(), result);
            for (ElsIfStatement elseIfStatement : ifStatement.getElsifs()) {
                ResultContext elseIfConfition = result.subContext();
                Expressions.getGenerator(scopeUnit, elseIfStatement.getCondition()).generate(elseIfConfition);
                result.writeLine("else if (" + elseIfConfition.toString() + ")");
                generateSubStatements(elseIfStatement.getStatements(), result);
            }
            if (!ifStatement.getElseStatements().isEmpty()) {
                result.writeLine("else");
                generateSubStatements(ifStatement.getElseStatements(), result);
            }
        } else {
            result.writeLine("if (" + ifCondition.toString() + ") {");
            generateSubStatements(ifStatement.getThenStatements(), result);
            for (ElsIfStatement elseIfStatement : ifStatement.getElsifs()) {
                ResultContext elseIfConfition = result.subContext();
                Expressions.getGenerator(scopeUnit, elseIfStatement.getCondition()).generate(elseIfConfition);
                result.writeLine("} else if (" + elseIfConfition.toString() + ") {");
                generateSubStatements(elseIfStatement.getStatements(), result);
            }
            if (!ifStatement.getElseStatements().isEmpty()) {
                result.writeLine("} else {");
                generateSubStatements(ifStatement.getElseStatements(), result);
            }
            result.writeLine("}");
        }
    }
    
    private boolean isSimple() {
        if (ifStatement.getThenStatements().size() != 1 || containsContainer(ifStatement.getThenStatements()))
            return false;
        if (ifStatement.getElseStatements().size() > 1 || containsContainer(ifStatement.getElseStatements()))
            return false;
        for (ElsIfStatement elseIfStatement : ifStatement.getElsifs()) {
            if (elseIfStatement.getStatements().size() > 1 || containsContainer(elseIfStatement.getStatements()))
                return false;
        }
        return true;
    }
    
    private boolean containsContainer(List<IStatement> statements) {
        for (IStatement statement : statements) {
            if (statement instanceof IStatementsContainer)
                return true;
        }
        return false;
    }
    
    private void generateSubStatements(List<IStatement> statements, ResultContext result) {
        result.incIndent();
        for (IStatement statement : statements)
            Statements.generate(scopeUnit, statement, result);
        result.decIndent();
    }

}
