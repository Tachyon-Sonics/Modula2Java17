package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ActualParametersContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.AssignmentOrProcCallContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CaseLabelListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CaseLabelsContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CaseStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CcaseContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DesignatorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DesignatorTailContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ExpListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ForStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IfStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.LoopStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.QualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.RepeatStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StatementSequenceContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.WhileStatementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.WithStatementContext;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.QualifiedIdentifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.ProcedureImplementationScope;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.CaseItem;
import ch.pitchtech.modula.converter.model.statement.CaseLabel;
import ch.pitchtech.modula.converter.model.statement.CaseStatement;
import ch.pitchtech.modula.converter.model.statement.ElsIfStatement;
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

public class StatementsProcessor extends ProcessorBase {

    public List<IStatement> processStatementSequence(IHasScope scopeUnit, StatementSequenceContext statementSequence) {
        List<IStatement> result = new ArrayList<>();
        for (int i = 0; i < statementSequence.getChildCount(); i++) {
            ParseTree node = statementSequence.getChild(i);
            if (node instanceof StatementContext statementContext) {
                if (statementContext.getChildCount() > 0) { // Skip any extra ";"
                    IStatement statement = processStatement(scopeUnit, statementContext);
                    result.add(statement);
                }
            } else if (node instanceof TerminalNode terminal) {
                expectText(terminal, ";");
            } else {
                throw new UnexpectedTokenException(node);
            }
        }
        return result;
    }
    
    private static ProcedureImplementation getEnclosingProcedure(IScope scope) {
        while (scope != null) {
            if (scope instanceof ProcedureImplementationScope procedureScope)
                return procedureScope.getProcedureImplementation();
            scope = scope.getParentScope();
        }
        return null;
    }
    
    private IStatement processStatement(IHasScope scopeUnit, StatementContext statementContext) {
        ParseTree node = statementContext.getChild(0);
        if (node instanceof TerminalNode terminal) {
            String text = terminal.getText();
            if (text.equals("RETURN")) {
                ProcedureImplementation procedureImplementation = getEnclosingProcedure(scopeUnit.getLocalScope());
                if (procedureImplementation == null)
                    throw new UnexpectedTokenException(statementContext, "RETURN only allowed in a PROCEDURE");
                if (statementContext.getChildCount() > 1) {
                    expectNbChild(statementContext, 2);
                    expect(statementContext, 1, ExpressionContext.class);
                    ExpressionContext expressionContext = (ExpressionContext) statementContext.getChild(1);
                    IExpression expr = new ExpressionsProcessor().processExpression(scopeUnit, expressionContext);
                    return new ReturnStatement(loc(terminal), procedureImplementation, expr);
                } else {
                    return new ReturnStatement(loc(terminal), procedureImplementation);
                }
            } else if (text.equals("EXIT")) {
                expectNbChild(statementContext, 1);
                return new ProcedureCall(loc(terminal), scopeUnit, null, BuiltInProcedure.EXIT.name());
            } else {
                throw new UnexpectedTokenException(terminal);
            }
        }
        
        expectNbChild(statementContext, 1);
        if (node instanceof WithStatementContext withStatementContext) {
            return processWithStatement(scopeUnit, withStatementContext);
        } else if (node instanceof IfStatementContext ifStatementContext) {
            return processIfStatement(scopeUnit, ifStatementContext);
        } else if (node instanceof AssignmentOrProcCallContext aopContext) {
            return processAssignmentOrProcCall(scopeUnit, aopContext);
        } else if (node instanceof WhileStatementContext whileStatementContext) {
            return processWhileStatement(scopeUnit, whileStatementContext);
        } else if (node instanceof RepeatStatementContext repeatStatementContext) {
            return processRepeatStatement(scopeUnit, repeatStatementContext);
        } else if (node instanceof LoopStatementContext loopStatementContext) {
            return processLoopStatement(scopeUnit, loopStatementContext);
        } else if (node instanceof ForStatementContext forStatementContext) {
            return processForStatement(scopeUnit, forStatementContext);
        } else if (node instanceof CaseStatementContext caseStatementContext) {
            return processCaseStatement(scopeUnit, caseStatementContext);
        } else {
            throw new UnexpectedTokenException(node);
        }
    }

    private IStatement processWithStatement(IHasScope scopeUnit, WithStatementContext withStatementContext) {
        expectNbChild(withStatementContext, 5);
        expect(withStatementContext, 0, "WITH");
        expect(withStatementContext, 1, DesignatorContext.class);
        expect(withStatementContext, 2, "DO");
        expect(withStatementContext, 3, StatementSequenceContext.class);
        expect(withStatementContext, 4, "END");
        DesignatorContext designatorContext = (DesignatorContext) withStatementContext.getChild(1);
        StatementSequenceContext statementSequence = (StatementSequenceContext) withStatementContext.getChild(3);
        
        IExpression expression = new ExpressionsProcessor().processExpression(scopeUnit, designatorContext);
        WithStatement withStatement = new WithStatement(loc(withStatementContext), scopeUnit, expression);
        List<IStatement> statements = processStatementSequence(withStatement, statementSequence);
        withStatement.addStatements(statements);
        return withStatement;
    }

    private IStatement processIfStatement(IHasScope scopeUnit, IfStatementContext ifStatementContext) {
        int nbChild = ifStatementContext.getChildCount();
        expect(ifStatementContext, 0, "IF");
        expect(ifStatementContext, 1, ExpressionContext.class);
        expect(ifStatementContext, 2, "THEN");
        expect(ifStatementContext, 3, StatementSequenceContext.class);
        expect(ifStatementContext, nbChild - 1, "END");
        ExpressionContext expressionContext = (ExpressionContext) ifStatementContext.getChild(1);
        StatementSequenceContext thenStatementSequence = (StatementSequenceContext) ifStatementContext.getChild(3);
        
        IExpression expression = new ExpressionsProcessor().processExpression(scopeUnit, expressionContext);
        IfStatement ifStatement = new IfStatement(loc(ifStatementContext), expression);
        List<IStatement> thenStatements = processStatementSequence(scopeUnit, thenStatementSequence);
        ifStatement.addThenStatements(thenStatements);
        int i = 4;
        while (i < nbChild) {
            String token = ifStatementContext.getChild(i).getText();
            if (token.equals("ELSIF")) {
                expect(ifStatementContext, i + 1, ExpressionContext.class);
                expect(ifStatementContext, i + 2, "THEN");
                expect(ifStatementContext, i + 3, StatementSequenceContext.class);
                ExpressionContext elsifExpressionContext = (ExpressionContext) ifStatementContext.getChild(i + 1);
                StatementSequenceContext elsifStatementSequence = (StatementSequenceContext) ifStatementContext.getChild(i + 3);
                
                IExpression elsifExpression = new ExpressionsProcessor().processExpression(scopeUnit, elsifExpressionContext);
                List<IStatement> elsifStatements = processStatementSequence(scopeUnit, elsifStatementSequence);
                ElsIfStatement elseIfStatement = new ElsIfStatement(loc(elsifExpressionContext), elsifExpression,
                        elsifStatements);
                ifStatement.addElsif(elseIfStatement);
                i+= 4;
            } else if (token.equals("ELSE")) {
                StatementSequenceContext statementSequence = (StatementSequenceContext) ifStatementContext.getChild(i + 1);
                List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
                ifStatement.addElseStatements(statements);
                i+= 2;
            } else if (token.equals("END")) {
                i++;
            } else {
                throw new UnexpectedTokenException(ifStatementContext.getChild(i));
            }
        }
        return ifStatement;
    }

    private IStatement processAssignmentOrProcCall(IHasScope scopeUnit, AssignmentOrProcCallContext aopContext) {
        if (aopContext.getChildCount() == 3 
                && aopContext.getChild(1).getText().equals(":=")) {
            // Assignement
            IExpression target = new ExpressionsProcessor().processExpression(scopeUnit, aopContext.getChild(0));
            IExpression value = new ExpressionsProcessor().processExpression(scopeUnit, aopContext.getChild(2));
            Assignement assignement = new Assignement(loc(aopContext), target, value);
            return assignement;
        } else if (aopContext.getChildCount() == 2 
                && (aopContext.getChild(0) instanceof DesignatorContext designatorContext)
                && (aopContext.getChild(1) instanceof ActualParametersContext actualParamsContext)) {
            // Procedure call
            if (designatorContext.getChildCount() == 1) {
                // Simple procedure
                expect(designatorContext, 0, QualidentContext.class);
                QualidentContext qualidentContext = (QualidentContext) designatorContext.getChild(0);
                return processProcedureCall(scopeUnit, qualidentContext, actualParamsContext);
            } else if (designatorContext.getChildCount() == 2) {
                // Expression of procedure type, followed by call
                expect(designatorContext, 0, QualidentContext.class);
                expect(designatorContext, 1, DesignatorTailContext.class);
                QualidentContext qualidentContext = (QualidentContext) designatorContext.getChild(0);
                DesignatorTailContext designatorTailContext = (DesignatorTailContext) designatorContext.getChild(1);
                return processProcedureExpressionCall(scopeUnit, qualidentContext, designatorTailContext, actualParamsContext);
            } else {
                throw new UnexpectedTokenException(designatorContext);
            }
        } else if (aopContext.getChildCount() == 1
                && (aopContext.getChild(0) instanceof DesignatorContext designatorContext)) {
            // Procedure call - no argument
            expectNbChild(designatorContext, 1);
            expect(designatorContext, 0, QualidentContext.class);
            QualidentContext qualidentContext = (QualidentContext) designatorContext.getChild(0);
            return processProcedureCall(scopeUnit, qualidentContext, null);
        } else {
            throw new UnexpectedTokenException(aopContext);
        }
    }

    private IStatement processWhileStatement(IHasScope scopeUnit, WhileStatementContext whileStatementContext) {
        expectNbChild(whileStatementContext, 5);
        expect(whileStatementContext, 0, "WHILE");
        expect(whileStatementContext, 1, ExpressionContext.class);
        expect(whileStatementContext, 2, "DO");
        expect(whileStatementContext, 3, StatementSequenceContext.class);
        expect(whileStatementContext, 4, "END");
        ExpressionContext expressionContext = (ExpressionContext) whileStatementContext.getChild(1);
        StatementSequenceContext statementSequence = (StatementSequenceContext) whileStatementContext.getChild(3);
        IExpression expr = new ExpressionsProcessor().processExpression(scopeUnit, expressionContext);
        List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
        WhileLoop whileLoop = new WhileLoop(loc(whileStatementContext), expr);
        whileLoop.addStatements(statements);
        return whileLoop;
    }
    
    private IStatement processRepeatStatement(IHasScope scopeUnit, RepeatStatementContext repeatStatementContext) {
        expectNbChild(repeatStatementContext, 4);
        expect(repeatStatementContext, 0, "REPEAT");
        expect(repeatStatementContext, 1, StatementSequenceContext.class);
        expect(repeatStatementContext, 2, "UNTIL");
        expect(repeatStatementContext, 3, ExpressionContext.class);
        ExpressionContext expressionContext = (ExpressionContext) repeatStatementContext.getChild(3);
        StatementSequenceContext statementSequence = (StatementSequenceContext) repeatStatementContext.getChild(1);
        IExpression expr = new ExpressionsProcessor().processExpression(scopeUnit, expressionContext);
        List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
        RepeatLoop repeatLoop = new RepeatLoop(loc(repeatStatementContext), expr);
        repeatLoop.addStatements(statements);
        return repeatLoop;
    }
    
    private IStatement processLoopStatement(IHasScope scopeUnit, LoopStatementContext loopStatementContext) {
        expectNbChild(loopStatementContext, 3);
        expect(loopStatementContext, 0, "LOOP");
        expect(loopStatementContext, 1, StatementSequenceContext.class);
        expect(loopStatementContext, 2, "END");
        StatementSequenceContext statementSequence = (StatementSequenceContext) loopStatementContext.getChild(1);
        List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
        LoopLoop loopLoop = new LoopLoop(loc(loopStatementContext));
        loopLoop.addStatements(statements);
        return loopLoop;
    }
    
    private IStatement processForStatement(IHasScope scopeUnit, ForStatementContext forStatementContext) {
        // FOR ident ':=' expression TO expression (BY constExpression)? DO statementSequence END
        int nbChild = forStatementContext.getChildCount();
        boolean hasBy = (nbChild == 11);
        expect(forStatementContext, 0, "FOR");
        expect(forStatementContext, 1, IdentContext.class);
        expect(forStatementContext, 2, ":=");
        expect(forStatementContext, 3, ExpressionContext.class);
        expect(forStatementContext, 4, "TO");
        expect(forStatementContext, 5, ExpressionContext.class);
        if (hasBy) {
            expect(forStatementContext, 6, "BY");
            expect(forStatementContext, 7, ConstExpressionContext.class);
        }
        expect(forStatementContext, nbChild - 3, "DO");
        expect(forStatementContext, nbChild - 2, StatementSequenceContext.class);
        expect(forStatementContext, nbChild - 1, "END");
        
        IdentContext identContext = (IdentContext) forStatementContext.getChild(1);
        ExpressionContext fromExpressionContext = (ExpressionContext) forStatementContext.getChild(3);
        ExpressionContext toExpressionContext = (ExpressionContext) forStatementContext.getChild(5);
        ExpressionsProcessor expressionProcessor = new ExpressionsProcessor();
        Identifier identifier = new Identifier(loc(identContext), scopeUnit, identContext.getText());
        IExpression fromExpression = expressionProcessor.processExpression(scopeUnit, fromExpressionContext);
        IExpression toExpression = expressionProcessor.processExpression(scopeUnit, toExpressionContext);
        IExpression byExpression = null;
        if (hasBy) {
            ConstExpressionContext byExpressionContext = (ConstExpressionContext) forStatementContext.getChild(7);
            byExpression = expressionProcessor.processExpression(scopeUnit, byExpressionContext);
        }
        StatementSequenceContext statementSequence = (StatementSequenceContext) forStatementContext.getChild(nbChild - 2);
        List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
        ForLoop forLoop = new ForLoop(loc(forStatementContext), identifier, fromExpression, toExpression, byExpression);
        forLoop.addStatements(statements);
        return forLoop;
    }
    
    private CaseStatement processCaseStatement(IHasScope scopeUnit, CaseStatementContext caseStatementContext) {
        int nbChild = caseStatementContext.getChildCount();
        expect(caseStatementContext, 0, "CASE");
        expect(caseStatementContext, 1, ExpressionContext.class);
        expect(caseStatementContext, 2, "OF");
        expect(caseStatementContext, nbChild - 1, "END");
        
        ExpressionContext expressionContext = (ExpressionContext) caseStatementContext.getChild(1);
        IExpression expression = new ExpressionsProcessor().processExpression(scopeUnit, expressionContext);
        CaseStatement caseStatement = new CaseStatement(loc(caseStatementContext), expression);
        
        for (int i = 3; i < nbChild - 1; i++) {
            ParseTree node = caseStatementContext.getChild(i);
            if (node instanceof TerminalNode terminal) {
                String text = terminal.getText();
                if (text.equals("ELSE")) {
                    if (i != nbChild - 3)
                        throw new UnexpectedTokenException(terminal, "ELSE must be the last clause");
                    expect(caseStatementContext, i + 1, StatementSequenceContext.class);
                    StatementSequenceContext statementSequence = (StatementSequenceContext) caseStatementContext.getChild(i + 1);
                    List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
                    caseStatement.setElseStatements(statements);
                    break; // ELSE can only be at the end of the case
                } else {
                    expectText(terminal, "|");
                }
            } else if (node instanceof CcaseContext caseContext) {
                expectNbChild(caseContext, 3);
                expect(caseContext, 0, CaseLabelListContext.class);
                expect(caseContext, 1, ":");
                expect(caseContext, 2, StatementSequenceContext.class);
                
                List<CaseLabel> caseLabels = new ArrayList<>();
                CaseLabelListContext caseLabelListContext = (CaseLabelListContext) caseContext.getChild(0);
                for (int j = 0; j < caseLabelListContext.getChildCount(); j++) {
                    ParseTree item = caseLabelListContext.getChild(j);
                    if (item instanceof TerminalNode terminal) {
                        expectText(terminal, ",");
                    } else {
                        expect(caseLabelListContext, j, CaseLabelsContext.class);
                        CaseLabelsContext caseLabelsContext = (CaseLabelsContext) caseLabelListContext.getChild(j);
                        if (caseLabelsContext.getChildCount() == 1) {
                            // Label
                            expect(caseLabelsContext, 0, ConstExpressionContext.class);
                            ConstExpressionContext constExpressionContext = (ConstExpressionContext) caseLabelsContext.getChild(0);
                            IExpression labelExpr = new ExpressionsProcessor().processExpression(scopeUnit, constExpressionContext);
                            CaseLabel caseLabel = new CaseLabel(labelExpr);
                            caseLabels.add(caseLabel);
                        } else {
                            // Range
                            expectNbChild(caseLabelsContext, 3);
                            expect(caseLabelsContext, 0, ConstExpressionContext.class);
                            expect(caseLabelsContext, 1, "..");
                            expect(caseLabelsContext, 2, ConstExpressionContext.class);
                            ConstExpressionContext constExpressionContext1 = (ConstExpressionContext) caseLabelsContext.getChild(0);
                            IExpression lowerExpr = new ExpressionsProcessor().processExpression(scopeUnit, constExpressionContext1);
                            ConstExpressionContext constExpressionContext2 = (ConstExpressionContext) caseLabelsContext.getChild(2);
                            IExpression upperExpr = new ExpressionsProcessor().processExpression(scopeUnit, constExpressionContext2);
                            CaseLabel caseLabel = new CaseLabel(lowerExpr, upperExpr);
                            caseLabels.add(caseLabel);
                        }
                    }
                }
                
                StatementSequenceContext statementSequence = (StatementSequenceContext) caseContext.getChild(2);
                List<IStatement> statements = processStatementSequence(scopeUnit, statementSequence);
                
                CaseItem caseItem = new CaseItem(caseLabels, statements);
                caseStatement.addCase(caseItem);
            } else {
                throw new UnexpectedTokenException(node);
            }
        }
        return caseStatement;
    }

    public ProcedureCall processProcedureCall(IHasScope scopeUnit, QualidentContext qualidentContext,
            ActualParametersContext actualParamsContext) {
        IExpression procedureId = new ExpressionsProcessor().processExpression(scopeUnit, qualidentContext);
        if (procedureId instanceof Identifier identifier) {
            String procedureName = identifier.getName();
            ProcedureCall procedureCall = new ProcedureCall(loc(qualidentContext), scopeUnit, null, procedureName);
            processProcedureArguments(scopeUnit, actualParamsContext, procedureCall::addArguments);
            return procedureCall;
        } else if (procedureId instanceof QualifiedIdentifier qi) {
            String moduleName = qi.getModule();
            String procedureName = qi.getName();
            ProcedureCall procedureCall = new ProcedureCall(loc(qualidentContext), qi.getScopeUnit(), moduleName, procedureName);
            processProcedureArguments(scopeUnit, actualParamsContext, procedureCall::addArguments);
            return procedureCall;
        } else {
            throw new UnexpectedTokenException(qualidentContext, "Expected a procedure name");
        }
    }
    
    public ProcedureExpressionCall processProcedureExpressionCall(IHasScope scopeUnit, QualidentContext qualidentContext,
            DesignatorTailContext designatorTailContext, ActualParametersContext actualParamsContext) {
        IExpression mainExpr = new ExpressionsProcessor().processExpression(scopeUnit, qualidentContext);
        IExpression procExpr = new ExpressionsProcessor().processDesignatorTail(scopeUnit, mainExpr, designatorTailContext);
        ProcedureExpressionCall procedureCall = new ProcedureExpressionCall(loc(qualidentContext), procExpr);
        processProcedureArguments(scopeUnit, actualParamsContext, procedureCall::addArguments);
        return procedureCall;
    }
    
    @FunctionalInterface
    public static interface ArgumentsAdder {
        public void addArguments(List<IExpression> arguments);
    }
    
    private void processProcedureArguments(IHasScope scopeUnit, ActualParametersContext actualParamsContext, ArgumentsAdder argumentsAdder) {
        if (actualParamsContext != null) {
            int nbChild = actualParamsContext.getChildCount();
            if (nbChild == 2) { // "procedure()"
                expect(actualParamsContext, 0, "(");
                expect(actualParamsContext, 1, ")");
                // No argument
            } else {
                expectNbChild(actualParamsContext, 3);
                expect(actualParamsContext, 0, "(");
                expect(actualParamsContext, 1, ExpListContext.class);
                expect(actualParamsContext, 2, ")");
                ExpListContext expListContext = (ExpListContext) actualParamsContext.getChild(1);
                List<IExpression> params = new ExpressionsProcessor().processExpressionList(scopeUnit, expListContext);
                argumentsAdder.addArguments(params);
            }
        }
    }
    
}
