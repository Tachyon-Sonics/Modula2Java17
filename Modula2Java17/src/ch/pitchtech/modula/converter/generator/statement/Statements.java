package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.CaseStatement;
import ch.pitchtech.modula.converter.model.statement.ForLoop;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.IfStatement;
import ch.pitchtech.modula.converter.model.statement.LoopLoop;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.statement.ProcedureExpressionCall;
import ch.pitchtech.modula.converter.model.statement.RepeatLoop;
import ch.pitchtech.modula.converter.model.statement.ReturnStatement;
import ch.pitchtech.modula.converter.model.statement.WhileLoop;
import ch.pitchtech.modula.converter.model.statement.WithStatement;

public class Statements {

    public static Generator getGenerator(IHasScope scopeUnit, IStatement statement) {
        if (statement instanceof Assignement assignment)
            return new AssignmentGenerator(scopeUnit, assignment);
        else if (statement instanceof ProcedureCall procedureCall)
            return new ProcedureCallGenerator(scopeUnit, procedureCall);
        else if (statement instanceof ProcedureExpressionCall procedureExpressionCall)
            return new ProcedureExpressionCallGenerator(scopeUnit, procedureExpressionCall);
        else if (statement instanceof WithStatement withStatement)
            return new WithStatementGenerator(scopeUnit, withStatement);
        else if (statement instanceof IfStatement ifStatement)
            return new IfStatementGenerator(scopeUnit, ifStatement);
        else if (statement instanceof CaseStatement caseStatement)
            return new CaseStatementGenerator(scopeUnit, caseStatement);
        else if (statement instanceof WhileLoop whileLoop)
            return new WhileLoopGenerator(scopeUnit, whileLoop);
        else if (statement instanceof RepeatLoop repeatLoop)
            return new RepeatLoopGenerator(scopeUnit, repeatLoop);
        else if (statement instanceof LoopLoop loopLoop)
            return new LoopLoopGenerator(scopeUnit, loopLoop);
        else if (statement instanceof ForLoop forLoop)
            return new ForLoopGenerator(scopeUnit, forLoop);
        else if (statement instanceof ReturnStatement returnStatement)
            return new ReturnStatementGenerator(scopeUnit, returnStatement);
        else
            throw new CompilerException(statement, "Unhandled " + statement.getClass().getSimpleName());
    }

}
