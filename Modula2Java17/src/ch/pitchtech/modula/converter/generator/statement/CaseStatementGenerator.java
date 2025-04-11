package ch.pitchtech.modula.converter.generator.statement;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.CaseItem;
import ch.pitchtech.modula.converter.model.statement.CaseLabel;
import ch.pitchtech.modula.converter.model.statement.CaseStatement;
import ch.pitchtech.modula.converter.model.statement.ElsIfStatement;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.IStatementsContainer;
import ch.pitchtech.modula.converter.model.statement.IfStatement;
import ch.pitchtech.modula.converter.model.statement.LoopLoop;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.statement.RepeatLoop;
import ch.pitchtech.modula.converter.model.statement.ReturnStatement;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class CaseStatementGenerator extends Generator {
    
    private final static boolean GENERATE_JAVA8_SWITCH = false;
    
    private final CaseStatement caseStatement;

    
    public CaseStatementGenerator(IHasScope scopeUnit, CaseStatement caseStatement) {
        super(scopeUnit, caseStatement);
        this.caseStatement = caseStatement;
    }

    @Override
    public void generate(ResultContext result) {
        if (GENERATE_JAVA8_SWITCH) {
            generateJava8(result);
        } else if (isSimple(caseStatement)) {
            generateJava17Simplified(result);
        } else {
            generateJava17(result);
        }
    }
     
    private void generateJava17(ResultContext result) {
        ResultContext exprContext = result.subContext();
        Expressions.getGenerator(scopeUnit, caseStatement.getExpression()).generate(exprContext);
        result.writeLine("switch (" + exprContext.toString() + ") {");
        result.incIndent();
        
        IType expressionType = result.resolveType(caseStatement.getExpression());
        for (CaseItem caseItem : caseStatement.getCases()) {
            result.writeIndent();
            result.write("case ");
            List<CaseLabel> labels = caseItem.labels();
            List<IExpression> expressions = new ArrayList<>();
            for (CaseLabel caseLabel : labels) {
                if (caseLabel.toExpr() == null)
                    expressions.add(caseLabel.expr());
                else
                    expressions.addAll(resolveRange(caseLabel, result));
            }
            for (int i = 0; i < expressions.size(); i++) {
                if (i > 0)
                    result.write(", ");
                IExpression expression = expressions.get(i);
                if (expressionType instanceof EnumerationType && expression instanceof Identifier identifier) {
                    // Special case: Java wants unqualified constants when switch is used on an enum
                    result.write(identifier.getName());
                } else {
                    IType valueType = result.resolveType(expression);
                    AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, expressionType, expressions.get(i), 
                            valueType, true, false);
                }
            }
            result.write(" -> {");
            result.writeLn();
            
            result.incIndent();
            List<IStatement> statements = caseItem.statements();
            for (IStatement statement : statements)
                Statements.getGenerator(scopeUnit, statement).generate(result);
            
            result.decIndent();
            result.writeLine("}");
        }
        
        if (caseStatement.getElseStatements() != null) {
            result.writeIndent();
            result.write("default -> {");
            result.writeLn();
            
            result.incIndent();
            for (IStatement statement : caseStatement.getElseStatements())
                Statements.getGenerator(scopeUnit, statement).generate(result);
            
            result.decIndent();
            result.writeLine("}");
        } else {
            // TODO figure out situations in which this can be removed (like not the last statement in a function)
            result.writeLine("default -> throw new RuntimeException(\"Unhandled CASE value \" + " + exprContext.toString() + ");");
        }
        
        result.decIndent();
        result.writeLine("}");
    }
    
    private void generateJava17Simplified(ResultContext result) {
        ResultContext exprContext = result.subContext();
        Expressions.getGenerator(scopeUnit, caseStatement.getExpression()).generate(exprContext);
        result.writeLine("switch (" + exprContext.toString() + ") {");
        result.incIndent();
        
        IType expressionType = result.resolveType(caseStatement.getExpression());
        for (CaseItem caseItem : caseStatement.getCases()) {
            result.writeIndent();
            result.write("case ");
            List<CaseLabel> labels = caseItem.labels();
            List<IExpression> expressions = new ArrayList<>();
            for (CaseLabel caseLabel : labels) {
                if (caseLabel.toExpr() == null)
                    expressions.add(caseLabel.expr());
                else
                    expressions.addAll(resolveRange(caseLabel, result));
            }
            for (int i = 0; i < expressions.size(); i++) {
                if (i > 0)
                    result.write(", ");
                IExpression expression = expressions.get(i);
                if (expressionType instanceof EnumerationType && expression instanceof Identifier identifier) {
                    // Special case: Java wants unqualified constants when switch is used on an enum
                    result.write(identifier.getName());
                } else {
                    IType valueType = result.resolveType(expression);
                    AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, expressionType, expressions.get(i), 
                            valueType, true, false);
                }
            }
            result.write(" -> ");
            
            ResultContext statementContext = result.subContext();
            List<IStatement> statements = caseItem.statements();
            for (IStatement statement : statements)
                Statements.getGenerator(scopeUnit, statement).generate(statementContext);
            result.write(statementContext.toString().trim());
            result.writeLn();
        }
        
        if (caseStatement.getElseStatements() != null) {
            result.writeIndent();
            result.write("default -> ");
            
            ResultContext statementContext = result.subContext();
            for (IStatement statement : caseStatement.getElseStatements())
                Statements.getGenerator(scopeUnit, statement).generate(statementContext);
            result.write(statementContext.toString().trim());
            result.writeLn();
        } else {
            result.writeLine("default -> throw new RuntimeException(\"Unhandled CASE value \" + " + exprContext.toString() + ");");
        }
        
        result.decIndent();
        result.writeLine("}");
    }
    
    public void generateJava8(ResultContext result) {
        ResultContext exprContext = result.subContext();
        Expressions.getGenerator(scopeUnit, caseStatement.getExpression()).generate(exprContext);
        result.writeLine("switch (" + exprContext.toString() + ") {");
        result.incIndent();
        
        IType expressionType = result.resolveType(caseStatement.getExpression());
        for (CaseItem caseItem : caseStatement.getCases()) {
            List<CaseLabel> labels = caseItem.labels();
            List<IExpression> expressions = new ArrayList<>();
            for (CaseLabel caseLabel : labels) {
                if (caseLabel.toExpr() == null)
                    expressions.add(caseLabel.expr());
                else
                    expressions.addAll(resolveRange(caseLabel, result));
            }
            for (int i = 0; i < expressions.size(); i++) {
                result.writeIndent();
                result.write("case ");
                IExpression expression = expressions.get(i);
                if (expressionType instanceof EnumerationType && expression instanceof Identifier identifier) {
                    // Special case: Java wants unqualified constants when switch is used on an enum
                    result.write(identifier.getName());
                } else {
                    IType valueType = result.resolveType(expression);
                    AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, expressionType, expressions.get(i), 
                            valueType, true, false);
                }
                result.write(":");
                if (i == expressions.size() - 1)
                    result.write(" {");
                result.writeLn();
            }
            
            result.incIndent();
            List<IStatement> statements = caseItem.statements();
            for (IStatement statement : statements)
                Statements.getGenerator(scopeUnit, statement).generate(result);
            
            if (!isAlwaysReturning(statements))
                result.writeLine("break;");
            result.decIndent();
            result.writeLine("}");
        }
        
        if (caseStatement.getElseStatements() != null) {
            result.writeIndent();
            result.write("default: {");
            result.writeLn();
            
            result.incIndent();
            for (IStatement statement : caseStatement.getElseStatements())
                Statements.getGenerator(scopeUnit, statement).generate(result);
            
            result.decIndent();
            result.writeLine("}");
        } else {
            result.writeLine("default: throw new RuntimeException(\"Unhandled CASE value \" + " + exprContext.toString() + ");");
        }
        
        result.decIndent();
        result.writeLine("}");
    }
    
    private List<IExpression> resolveRange(CaseLabel caseLabel, ResultContext context) {
        List<IExpression> result = new ArrayList<>();
        IExpression fromExpr = caseLabel.expr();
        IExpression toExpr = caseLabel.toExpr();
        IType fromType = context.resolveType(fromExpr);
        if (fromType instanceof LiteralType literalType && literalType.isBuiltIn()) {
            BuiltInType biType = BuiltInType.valueOf(literalType.getName());
            if (biType.isNumeric() && !biType.isDecimal()) {
                // Numeric range
                Object lower0 = fromExpr.evaluateConstant();
                Object upper0 = fromExpr.evaluateConstant();
                if (lower0 instanceof Number lower && upper0 instanceof Number upper) {
                    for (long value = lower.longValue(); value <= upper.longValue(); value++) {
                        ConstantLiteral valueExpr = new ConstantLiteral(caseStatement.getSourceLocation(), String.valueOf(value));
                        result.add(valueExpr);
                    }
                } else {
                    throw new CompilationException(caseStatement, "Unable to evaluate numeric case interval {0}..{1}", fromExpr, toExpr);
                }
            } else {
                throw new CompilationException(caseStatement, "Unable to evaluate case interval {0}..{1}", fromExpr, toExpr);
            }
        } else if (fromType instanceof EnumerationType fromEnumType) {
            // Enumerated range
            if (fromExpr instanceof Identifier fromIdentifier && toExpr instanceof Identifier toIdentifier) {
                String lowerName = fromIdentifier.getName();
                String upperName = toIdentifier.getName();
                int lowerIndex = fromEnumType.getElements().indexOf(lowerName);
                int upperIndex = fromEnumType.getElements().indexOf(upperName);
                if (lowerIndex >= 0 && upperIndex >= 0) {
                    for (int index = lowerIndex; index <= upperIndex; index++) {
                        Identifier item = new Identifier(
                                fromIdentifier.getSourceLocation(),
                                fromIdentifier.getScopeUnit(),
                                fromEnumType.getElements().get(index));
                        result.add(item);
                    }
                } else {
                    throw new CompilationException(caseStatement, "Unable to evaluate enumerated case interval {0}..{1} of type {2}",
                            fromExpr, toExpr, fromEnumType);
                }
            } else {
                throw new CompilationException(caseStatement, "Unable to evaluate enumerated case interval {0}..{1}", fromExpr, toExpr);
            }
        }
        return result;
    }
    
    private static boolean isSimple(CaseStatement caseStatement) {
        for (CaseItem caseItem : caseStatement.getCases()) {
            if (caseItem.statements().size() != 1)
                return false;
            IStatement statement = caseItem.statements().get(0);
            if (statement instanceof IStatementsContainer || statement instanceof ReturnStatement)
                return false;
        }
        if (caseStatement.getElseStatements() != null) {
            if (caseStatement.getElseStatements().size() != 1)
                return false;
            IStatement statement = caseStatement.getElseStatements().get(0);
            if (statement instanceof IStatementsContainer || statement instanceof ReturnStatement)
                return false;
        }
        return true;
    }
    
    /*
     * Whether the given statements always end by a "RETURN" statement.
     * Somehow incomplete... if or while with constant boolean expressions are not handled.
     */
    private static boolean isAlwaysReturning(List<IStatement> statements) {
        for (IStatement statement : statements) {
            if (statement instanceof ReturnStatement) {
                return true;
            } else if (statement instanceof IfStatement ifStatement) {
                if (isAlwaysReturning(ifStatement))
                    return true;
            } else if (statement instanceof CaseStatement caseStatement) {
                if (isAlwaysReturning(caseStatement))
                    return true;
            } else if (statement instanceof LoopLoop loopLoop) {
                if (isAlwaysReturning(loopLoop.getStatements()))
                    return true;
            } else if (statement instanceof RepeatLoop repeatLoop) {
                if (isAlwaysReturning(repeatLoop.getStatements()))
                    return true;
            } else if (statement instanceof ProcedureCall procedureCall && procedureCall.getProcedureName().equals("EXIT")) {
                return false;
            }
        }
        return false;
    }
    
    private static boolean isAlwaysReturning(IfStatement ifStatement) {
        if (!isAlwaysReturning(ifStatement.getThenStatements()))
            return false;
        if (!isAlwaysReturning(ifStatement.getElseStatements()))
            return false;
        for (ElsIfStatement elsifStatement : ifStatement.getElsifs()) {
            if (!isAlwaysReturning(elsifStatement.getStatements()))
                return false;
        }
        return true;
    }
    
    private static boolean isAlwaysReturning(CaseStatement caseStatement) {
        for (CaseItem caseItem : caseStatement.getCases()) {
            if (!isAlwaysReturning(caseItem.statements()))
                return false;
        }
        if (caseStatement.getElseStatements() != null) {
            if (!isAlwaysReturning(caseStatement.getElseStatements()))
                return false;
        }
        return true;
    }

}
