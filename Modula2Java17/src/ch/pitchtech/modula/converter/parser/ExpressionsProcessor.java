package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ActualParametersContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.AddOperatorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstFactorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstTermContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DesignatorTailContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ElementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ExpListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FactorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.MulOperatorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.NumberContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.QualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.RelationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SetOrDesignatorOrProcCallContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SetOrQualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.Set_Context;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SimpleConstExprContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SimpleExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StringContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.TermContext;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.Dereference;
import ch.pitchtech.modula.converter.model.expression.QualifiedAccess;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression.SetRange;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class ExpressionsProcessor extends ProcessorBase {

    public List<IExpression> processExpressionList(IHasScope scopeUnit, ExpListContext expListContext) {
        List<IExpression> result = new ArrayList<>();
        for (int i = 0; i < expListContext.getChildCount(); i++) {
            ParseTree paramNode = expListContext.getChild(i);
            if (paramNode instanceof ExpressionContext expressionContext) {
                IExpression paramExpr = processExpression(scopeUnit, expressionContext);
                result.add(paramExpr);
            } else if (paramNode instanceof TerminalNode terminal) {
                expectText(terminal, ",");
            } else {
                throw new UnexpectedTokenException(paramNode);
            }
        }
        return result;
    }
    
    private int parenthesesCount = 0;
    
    public IExpression processExpression(IHasScope scopeUnit, ParseTree expressionContext) {
        IExpression result = null;
        boolean qualify = false; // Whether a "." was hit
        int i = 0;
        while (i < expressionContext.getChildCount()) {
            ParseTree node = expressionContext.getChild(i);
            if (node instanceof IdentContext identContext) {
                expectNbChild(identContext, 1);
                Identifier identifier = new Identifier(loc(identContext), scopeUnit, identContext.getText());
                if (result instanceof Identifier prefix) {
                    if (qualify) {
                        Identifier qualifier = new Identifier(loc(identContext), scopeUnit, prefix.getName());
                        result = new QualifiedAccess(loc(identContext), qualifier, identifier);
                        qualify = false;
                    } else {
                        throw new UnexpectedTokenException(identContext, "Identifier not expected after expression " + result);
                    }
                } else {
                    result = identifier;
                }
            } else if (node instanceof QualidentContext qualidentContext) {
                if (result != null)
                    throw new UnexpectedTokenException(qualidentContext, "Identifier not expected after expression " + result);
                int nbChild = qualidentContext.getChildCount(); // TODO put this logic for all QualidentContext
                IExpression curExpr = null;
                for (int j = 0; j < nbChild; j++) {
                    ParseTree item = qualidentContext.getChild(j);
                    if (item instanceof IdentContext identContext) {
                        String name = identContext.getText();
                        Identifier identifier = new Identifier(loc(identContext), scopeUnit, name);
                        if (curExpr == null)
                            curExpr = identifier;
                        else
                            curExpr = new QualifiedAccess(loc(identContext), curExpr, identifier);
                    } else if (item instanceof TerminalNode terminal) {
                        expectText(terminal, ".");
                    } else {
                        throw new UnexpectedTokenException(node);
                    }
                }
                result = curExpr;
            } else if (node instanceof DesignatorTailContext designatorTailContext) {
                if (result == null)
                    throw new UnexpectedTokenException(designatorTailContext);
                result = processDesignatorTail(scopeUnit, result, designatorTailContext);
            } else if (node instanceof ExpressionContext
                    || node instanceof SimpleExpressionContext
                    || node instanceof SimpleConstExprContext
                    || node instanceof TermContext
                    || node instanceof ConstTermContext
                    || node instanceof FactorContext
                    || node instanceof ConstFactorContext) {
                if (result != null)
                    throw new UnexpectedTokenException(node, "Expression not expected after expression " + result);
                result = processExpression(scopeUnit, node);
            } else if (node instanceof SetOrDesignatorOrProcCallContext sdpContext) {
                if (result != null)
                    throw new UnexpectedTokenException(sdpContext, "Expression not expected after expression " + result);
                if (sdpContext.getChildCount() == 1 && sdpContext.getChild(0) instanceof QualidentContext) {
                    // Expression
                    result = processExpression(scopeUnit, sdpContext);
                } else if (sdpContext.getChildCount() == 2 
                        && sdpContext.getChild(0) instanceof QualidentContext qualidentContext
                        && sdpContext.getChild(1) instanceof ActualParametersContext actualParamsContext) {
                    // Procedure call
                    ProcedureCall procedureCall = new StatementsProcessor().processProcedureCall(scopeUnit, qualidentContext, actualParamsContext);
                    result = procedureCall.asFunction();
                } else if (sdpContext.getChildCount() == 2 
                        && sdpContext.getChild(0) instanceof QualidentContext qualidentContext
                        && sdpContext.getChild(1) instanceof DesignatorTailContext designatorTailContext) {
                    result = processExpression(scopeUnit, qualidentContext);
                    result = processDesignatorTail(scopeUnit, result, designatorTailContext);
                } else if (sdpContext.getChildCount() == 2 
                        && sdpContext.getChild(0) instanceof QualidentContext qualidentContext
                        && sdpContext.getChild(1) instanceof Set_Context setContext) {
                    // SomeSet{setValue1, setValue2, ...}
                    SetExpression setExpression = processSetExpression(scopeUnit, sdpContext, qualidentContext, setContext);
                    result = setExpression;
                } else {
                    throw new UnexpectedTokenException(sdpContext);
                }
            } else if (node instanceof SetOrQualidentContext setOrQualidentContext) {
                if (setOrQualidentContext.getChildCount() == 1) {
                    result = processExpression(scopeUnit, setOrQualidentContext.getChild(0));
                } else {
                    result = processSetConstantExpression(scopeUnit, setOrQualidentContext);
                }
            } else if (node instanceof NumberContext numberContext) {
                expectNbChild(numberContext, 1);
                expect(numberContext, 0, TerminalNode.class);
                String numberStr = numberContext.getText();
                ConstantLiteral constant = new ConstantLiteral(loc(numberContext), numberStr);
                result = constant;
            } else if (node instanceof RelationContext relationContext) {
                return processInfixExpression(scopeUnit, expressionContext, result, relationContext, i);
            } else if (node instanceof AddOperatorContext addOperatorContext) {
                return processInfixExpression(scopeUnit, expressionContext, result, addOperatorContext, i);
            } else if (node instanceof MulOperatorContext mulOperatorContext) {
                return processInfixExpression(scopeUnit, expressionContext, result, mulOperatorContext, i);
            } else if (node instanceof TerminalNode terminal) {
                String text = terminal.getText();
                if (text.equals("(")) {
                    parenthesesCount++;
                } else if (text.equals(")")) {
                    if (parenthesesCount <= 0 || result == null)
                        throw new UnexpectedTokenException(terminal);
                    parenthesesCount--;
                    result = new ParenthesedExpression(loc(terminal), result);
                } else if (text.equals("-")) {
                    IExpression expr = processExpression(scopeUnit, expressionContext.getChild(i + 1));
                    result = new MinusExpression(loc(terminal), expr);
                    i++; // One operand eaten
                } else if (text.equals("+")) {
                    result = processExpression(scopeUnit, expressionContext.getChild(i + 1));
                    i++; // One operand eaten
                } else if (BuiltInProcedure.getBuiltInFunctionNames().contains(text)) {
                    return processBuiltInFunctionCall(scopeUnit, expressionContext, terminal, text);
                } else if (text.equals("~")) {
                    return processBuiltInFunctionCall(scopeUnit, expressionContext, terminal, "NOT");
                } else if (text.equals("ADR(")) {
                    expectNbChild(expressionContext, i + 3);
                    expect(expressionContext, i + 1, ExpressionContext.class);
                    expect(expressionContext, i + 2, ")");
                    IExpression expr = processExpression(scopeUnit, expressionContext.getChild(i + 1));
                    FunctionCall functionCall = new FunctionCall(loc(expressionContext.getChild(i)), scopeUnit, null, BuiltInProcedure.ADR.name());
                    functionCall.addArgument(expr);
                    return functionCall;
                } else if (text.equals(".")) {
                    // Qualified access
                    qualify = true;
                } else {
                    throw new UnexpectedTokenException(terminal);
                }
            } else if (node instanceof StringContext stringContext) {
                expectNbChild(stringContext, 1);
                expect(stringContext, 0, TerminalNode.class);
                String theString = stringContext.getText();
                if ((!theString.startsWith("\"") || !theString.endsWith("\""))
                        && (!theString.startsWith("'") || !theString.endsWith("'")))
                    throw new UnexpectedTokenException(node);
                theString = theString.substring(1, theString.length() - 1);
                result = new StringLiteral(loc(node), theString);
            } else {
                throw new UnexpectedTokenException(node);
            }
            i++;
        }
        return result;
    }

    private SetExpression processSetExpression(IHasScope scopeUnit, SetOrDesignatorOrProcCallContext sdpContext, QualidentContext qualidentContext,
            Set_Context setContext) {
        expectNbChild(qualidentContext, 1);
        expect(qualidentContext, 0, IdentContext.class);
        
        // Name
        String setTypeName = qualidentContext.getText();
        IType setType = new LiteralType(loc(qualidentContext), scopeUnit, setTypeName, false);
        
        // Values
        int nbChild = setContext.getChildCount();
        expect(setContext, 0, "{");
        expect(setContext, nbChild - 1, "}");
        List<IExpression> setValues = new ArrayList<>();
        List<SetRange> setRanges = new ArrayList<>();
        for (int i = 1; i < nbChild - 1; i++) {
            ParseTree node = setContext.getChild(i);
            if (node instanceof TerminalNode terminal) {
                expectText(terminal, ",");
            } else if (node instanceof ElementContext elementContext) {
                int nbItems = elementContext.getChildCount();
                if (nbItems == 1) {
                    expect(elementContext, 0, ConstExpressionContext.class);
                    ConstExpressionContext constExpressionContext = (ConstExpressionContext) elementContext.getChild(0);
                    IExpression expression = processExpression(scopeUnit, constExpressionContext);
                    setValues.add(expression);
                } else {
                    // "element..element"
                    expectNbChild(elementContext, 3);
                    expect(elementContext, 0, ConstExpressionContext.class);
                    expect(elementContext, 1, "..");
                    expect(elementContext, 2, ConstExpressionContext.class);
                    ConstExpressionContext lowerContext = (ConstExpressionContext) elementContext.getChild(0);
                    IExpression lower = processExpression(scopeUnit, lowerContext);
                    ConstExpressionContext upperContext = (ConstExpressionContext) elementContext.getChild(2);
                    IExpression upper = processExpression(scopeUnit, upperContext);
                    SetRange range = new SetRange(lower, upper);
                    setRanges.add(range);
                }
            } else {
                throw new UnexpectedTokenException(node);
            }
        }
        SetExpression setExpression = new SetExpression(loc(sdpContext), setType, setValues);
        setExpression.addSetRanges(setRanges);
        return setExpression;
    }
    
    private IExpression processSetConstantExpression(IHasScope scopeUnit, SetOrQualidentContext setOrQualidentContext) {
        expectNbChild(setOrQualidentContext, 2);
        expect(setOrQualidentContext, 0, QualidentContext.class);
        expect(setOrQualidentContext, 1, Set_Context.class);
        QualidentContext qualidentContext = (QualidentContext) setOrQualidentContext.getChild(0);
        expectNbChild(qualidentContext, 1);
        expect(qualidentContext, 0, IdentContext.class);
        IdentContext identContext = (IdentContext) qualidentContext.getChild(0);
        String setTypeName = identContext.getText();
        Set_Context setContext = (Set_Context) setOrQualidentContext.getChild(1);
        int nbChild = setContext.getChildCount();
        expect(setContext, 0, "{");
        expect(setContext, nbChild - 1, "}");
        List<IExpression> setElements = new ArrayList<>();
        for (int i = 1; i < nbChild - 1; i++) {
            ParseTree setItem = setContext.getChild(i);
            if (setItem instanceof TerminalNode terminal) {
                expectText(terminal, ",");
            } else if (setItem instanceof ElementContext elementContext) {
                expectNbChild(elementContext, 1);
                expect(elementContext, 0, ConstExpressionContext.class);
                ConstExpressionContext constExpressionContext = (ConstExpressionContext) elementContext.getChild(0);
                IExpression element = processExpression(scopeUnit, constExpressionContext);
                setElements.add(element);
            } else {
                throw new UnexpectedTokenException(setItem);
            }
        }
        IType setType = new LiteralType(loc(setOrQualidentContext), scopeUnit, setTypeName, false);
        SetExpression setConstant = new SetExpression(loc(setContext), setType, setElements);
        return setConstant;
    }

    private IExpression processInfixExpression(IHasScope scopeUnit, ParseTree expressionContext, IExpression result,
            ParseTree operatorContext, int operatorChildIndex) {
        if (result == null)
            throw new UnexpectedTokenException(operatorContext, "Missing left-side expression for " + operatorContext);
        // <operand> <operator> <operand> [<operator> <operand>]*
        expectNbChild(operatorContext, 1);
        expect(operatorContext, 0, TerminalNode.class);
        
        String operator = operatorContext.getText();
        IExpression rightSide = processExpression(scopeUnit, expressionContext.getChild(operatorChildIndex + 1));
        InfixOpExpression infixExpr = new InfixOpExpression(loc(expressionContext), result, operator, rightSide);
        for (int i = operatorChildIndex + 2; i < expressionContext.getChildCount(); i+= 2) {
            operator = expressionContext.getChild(i).getText();
            rightSide = processExpression(scopeUnit, expressionContext.getChild(i + 1));
            infixExpr = new InfixOpExpression(loc(expressionContext.getChild(i)), infixExpr, operator, rightSide);
        }
        return infixExpr;
    }

    private IExpression processBuiltInFunctionCall(IHasScope scopeUnit, ParseTree expressionContext, TerminalNode terminal, String text) {
        // SIZE(...), MIN(...), NOT <expr> etc
        expectNbChild(expressionContext, 2); // terminal, factor context
        expect(expressionContext, 1, FactorContext.class);
        FactorContext factorContext = (FactorContext) expressionContext.getChild(1);
        if (factorContext.getChild(0) instanceof SetOrDesignatorOrProcCallContext) {
            // NOT <expr>
            expectNbChild(factorContext, 1);
            IExpression argument = processExpression(scopeUnit, factorContext);
            FunctionCall functionCall = new FunctionCall(loc(terminal), scopeUnit, null, text);
            functionCall.addArgument(argument);
            return functionCall;
        } else { // Operation(...)
            expect(factorContext, 0, "(");
            expect(factorContext, 1, ExpressionContext.class);
            expect(factorContext, 2, ")");
            ExpressionContext argumentExpressionContext = (ExpressionContext) factorContext.getChild(1);
            IExpression argument = processExpression(scopeUnit, argumentExpressionContext);
            FunctionCall functionCall = new FunctionCall(loc(terminal), scopeUnit, null, text);
            functionCall.addArgument(argument);
            return functionCall;
        }
    }

    public IExpression processDesignatorTail(IHasScope scopeUnit, IExpression mainExpr, DesignatorTailContext designatorTailContext) {
        // (('[' expList ']' | '^') ('.' ident)*) +
        int nbChild = designatorTailContext.getChildCount();
        IExpression result = mainExpr;
        boolean inArrayAccess = false;
        boolean inFieldAccess = false;
        for (int i = 0; i < nbChild; i++) {
            ParseTree node = designatorTailContext.getChild(i);
            if (node instanceof TerminalNode terminal) {
                String text = terminal.getText();
                if (text.equals("^")) {
                    result = new Dereference(loc(terminal), result);
                } else if (text.equals("[")) {
                    if (inArrayAccess)
                        throw new UnexpectedTokenException(terminal);
                    inArrayAccess = true;
                } else if (text.equals("]")) {
                    if (!inArrayAccess || result == null)
                        throw new UnexpectedTokenException(terminal);
                    inArrayAccess = false;
                } else if (text.equals(".")) {
                    if (inFieldAccess)
                        throw new UnexpectedTokenException(terminal);
                    inFieldAccess = true;
                } else {
                    throw new UnexpectedTokenException(terminal);
                }
            } else if (node instanceof IdentContext identContext) {
                if (!inFieldAccess || result == null)
                    throw new UnexpectedTokenException(identContext);
                inFieldAccess = false;
                String identName = identContext.getText();
                Identifier identifier = new Identifier(loc(identContext), scopeUnit, identName);
                result = new QualifiedAccess(loc(identContext), result, identifier);
            } else if (node instanceof ExpListContext expListContext) {
                if (!inArrayAccess)
                    throw new UnexpectedTokenException(expListContext);
                List<IExpression> indexes = processExpressionList(scopeUnit, expListContext);
                ArrayAccess arrayAccess = new ArrayAccess(loc(designatorTailContext), result);
                arrayAccess.addIndexes(indexes);
                result = arrayAccess;
            }
        }
        return result;
    }
    
}
