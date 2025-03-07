package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.AddOperatorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstFactorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstTermContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstantDeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ElementContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.MulOperatorContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.NumberContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.QualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SetOrQualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.Set_Context;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SimpleConstExprContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StringContext;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class ConstantDeclarationsProcessor extends ProcessorBase {
    
    private final IHasScope scopeUnit;

    
    public ConstantDeclarationsProcessor(IHasScope scopeUnit) {
        this.scopeUnit = scopeUnit;
    }

    public List<ConstantDefinition> process(DefinitionContext definition) {
        return processImpl(definition);
    }
    
    public List<ConstantDefinition> process(DeclarationContext definition) {
        return processImpl(definition);
    }
    
    private List<ConstantDefinition> processImpl(ParserRuleContext definition) {
        expect(definition, 0, "CONST");
        List<ConstantDefinition> result = new ArrayList<>();
        for (int i = 1; i < definition.getChildCount(); i++) {
            ParseTree item = definition.getChild(i);
            if (item instanceof ConstantDeclarationContext constantDeclaration) {
                expect(constantDeclaration, 0, IdentContext.class);
                expect(constantDeclaration, 1, "=");
                expect(constantDeclaration, 2, ConstExpressionContext.class);
                
                String name = constantDeclaration.getChild(0).getText();
                ConstExpressionContext constantExpression = (ConstExpressionContext) constantDeclaration.getChild(2);
                IExpression value = processConstantExpression(constantExpression);
                ConstantDefinition constantDefinition = new ConstantDefinition(loc(constantDeclaration), scopeUnit, name, value);
                result.add(constantDefinition);
            }
        }
        return result;
    }
    
    IExpression processConstantExpression(ConstExpressionContext constantExpression) { // TODO try reusing ExpressionsProcessor
        IExpression lastExpr = null;
        String operator = null;
        for (int i = 0; i < constantExpression.getChildCount(); i++) {
            ParseTree element = constantExpression.getChild(i);
            if (element instanceof TerminalNode terminal) {
                if (System.currentTimeMillis() > 0)
                    throw new UnexpectedTokenException(constantExpression, "Untested");
                if (operator != null)
                    throw new UnexpectedTokenException(constantExpression, "Two consecutive operators");
                operator = terminal.getText();
            } else {
                IExpression expr = processConstantElement(element);
                if (operator != null) {
                    assert lastExpr != null;
                    lastExpr = new InfixOpExpression(loc(constantExpression), lastExpr, operator, expr);
                    operator = null;
                } else {
                    lastExpr = expr;
                }
            }
        }
        return lastExpr;
    }

    private IExpression processConstantElement(ParseTree constantElement) {
        if (constantElement instanceof SimpleConstExprContext simpleConstExpr) {
            IExpression lastExpr = null;
            String operator = null;
            boolean minus = false;
            for (int i = 0; i < simpleConstExpr.getChildCount(); i++) {
                ParseTree element = simpleConstExpr.getChild(i);
                if (element instanceof TerminalNode terminal) {
                    if (terminal.getText().equals("-")) {
                        minus = true;
                    } else {
                        throw new UnexpectedTokenException(terminal);
                    }
                } else if (element instanceof AddOperatorContext addOperator) {
                    expectNbChild(addOperator, 1);
                    expect(addOperator, 0, TerminalNode.class);
                    TerminalNode terminal = (TerminalNode) addOperator.getChild(0);
                    if (lastExpr != null && operator == null) {
                        operator = terminal.getText();
                    } else {
                        throw new UnexpectedTokenException(terminal);
                    }
                } else {
                    IExpression expr = processSimpleConstExpression(element);
                    if (operator != null) {
                        assert lastExpr != null;
                        lastExpr = new InfixOpExpression(loc(element), lastExpr, operator, expr);
                        operator = null;
                    } else {
                        lastExpr = expr;
                    }
                }
            }
            if (minus)
                lastExpr = new MinusExpression(loc(simpleConstExpr), lastExpr);
            return lastExpr;
        } else {
            throw new UnexpectedTokenException(constantElement);
        }
    }

    private IExpression processSimpleConstExpression(ParseTree simpleConstElement) {
        if (simpleConstElement instanceof ConstTermContext constTerm) {
            return processConstTerm(constTerm);
        } else if (simpleConstElement instanceof QualidentContext qualidentContext) {
            expect(qualidentContext, 0, IdentContext.class);
            return processSimpleConstExpression(qualidentContext.getChild(0));
        } else if (simpleConstElement instanceof IdentContext identContext) {
            return new Identifier(loc(identContext), scopeUnit, identContext.getText());
        } else {
            throw new UnexpectedTokenException(simpleConstElement);
        }
    }

    private IExpression processConstTerm(ConstTermContext constTerm) {
        IExpression lastExpr = null;
        String operator = null;
        int nbChild = constTerm.getChildCount();
        for (int i = 0; i < nbChild; i++) {
            if (constTerm.getChild(i) instanceof ConstFactorContext constFactor) {
                IExpression expr = processConstFactor(constFactor);
                if (operator == null) {
                    lastExpr = expr;
                } else {
                    if (lastExpr == null)
                        throw new UnexpectedTokenException(constTerm);
                    lastExpr = new InfixOpExpression(loc(constTerm), lastExpr, operator, expr);
                    operator = null;
                }
            } else if (constTerm.getChild(i) instanceof MulOperatorContext mulOperator) {
                if (lastExpr == null)
                    throw new UnexpectedTokenException(mulOperator, "Missing left operand");
                if (operator != null)
                    throw new UnexpectedTokenException(mulOperator, "Extra operator " + operator);
                operator = mulOperator.getText();
            } else {
                throw new UnexpectedTokenException(constTerm.getChild(i));
            }
        }
        return lastExpr;
    }

    private int parenthesesCount = 0;
    
    private IExpression processConstFactor(ConstFactorContext constFactor) {
        IExpression result = null;
        int k = 0;
        while (k < constFactor.getChildCount()) {
            ParseTree factorElement = constFactor.getChild(k);
            if (factorElement instanceof NumberContext number) {
                result = new ConstantLiteral(loc(number), number.getText());
            } else if (factorElement instanceof TerminalNode terminal) {
                String text = terminal.getText();
                if (Set.of("MIN", "MAX", "ORD").contains(text)) {
                    String functionName = text;
                    String expression = constFactor.getChild(k + 1).getText();
                    if (!expression.startsWith("(") || !expression.endsWith(")"))
                        throw new UnexpectedTokenException(constFactor);
                    String typeName = expression.substring(1, expression.length() - 1);
                    FunctionCall functionCall = new FunctionCall(loc(terminal), scopeUnit, functionName);
                    functionCall.addArgument(new Identifier(loc(constFactor.getChild(k + 1)), scopeUnit, typeName));
                    result = functionCall;
                    k++;
                } else if (text.equals("(")) {
                    parenthesesCount++;
                } else if (text.equals(")")) {
                    if (parenthesesCount <= 0 || result == null)
                        throw new UnexpectedTokenException(terminal);
                    parenthesesCount--;
                    result = new ParenthesedExpression(loc(terminal), result);
                } else {
                    throw new UnexpectedTokenException(terminal);
                }
            } else if (factorElement instanceof SetOrQualidentContext setOrQualidentContext) {
                if (setOrQualidentContext.getChildCount() == 1) {
                    result = processSimpleConstExpression(setOrQualidentContext.getChild(k));
                } else {
                    expectNbChild(setOrQualidentContext, 2);
                    expect(setOrQualidentContext, 0, QualidentContext.class);
                    expect(setOrQualidentContext, 1, Set_Context.class);
                    QualidentContext qualidentContext = (QualidentContext) setOrQualidentContext.getChild(k);
                    expectNbChild(qualidentContext, 1);
                    expect(qualidentContext, 0, IdentContext.class);
                    IdentContext identContext = (IdentContext) qualidentContext.getChild(k);
                    String setTypeName = identContext.getText();
                    Set_Context setContext = (Set_Context) setOrQualidentContext.getChild(k + 1);
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
                            IExpression element = processConstantExpression(constExpressionContext);
                            setElements.add(element);
                        } else {
                            throw new UnexpectedTokenException(setItem);
                        }
                    }
                    IType setType = new LiteralType(loc(setOrQualidentContext), scopeUnit, setTypeName, false);
                    SetExpression enumSetConstant = new SetExpression(loc(setOrQualidentContext), setType, setElements);
                    result = enumSetConstant;
                    k++;
                }
            } else if (factorElement instanceof ConstExpressionContext constExpressionContext) {
                result = processConstantExpression(constExpressionContext);
            } else if (factorElement instanceof StringContext stringContext) {
                expectNbChild(stringContext, 1);
                expect(stringContext, 0, TerminalNode.class);
                String theString = stringContext.getText();
                if ((!theString.startsWith("\"") || !theString.endsWith("\""))
                        && (!theString.startsWith("'") || !theString.endsWith("'")))
                    throw new UnexpectedTokenException(stringContext);
                theString = theString.substring(1, theString.length() - 1);
                result = new StringLiteral(loc(stringContext), theString);
            } else {
                throw new UnexpectedTokenException(factorElement);
            }
            k++;
        }
        return result;
    }
    
}
