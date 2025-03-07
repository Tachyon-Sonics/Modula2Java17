package ch.pitchtech.modula.converter.parser;

import java.util.Arrays;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public abstract class ProcessorBase {
    
    protected final void expectNbChild(ParseTree node, int expected) {
        int actual = node.getChildCount();
        if (actual != expected)
            throw new UnexpectedTokenException("Expected number of children: " + expected + ", found: " + actual, node);
    }
    
    protected final void expect(ParseTree node, int childIndex, String expectedText) {
        String actualText = node.getChild(childIndex).getText();
        if (!actualText.equals(expectedText)) {
            String lineInfo = "";
            if (node instanceof ParserRuleContext prc) {
                lineInfo = " at " + prc.getStart().getLine() + ":" + prc.getStart().getCharPositionInLine();
            }
            throw new UnexpectedTokenException(node, "Expected: '" + expectedText + "'; found: '" + actualText + "'" + lineInfo);
        }
    }
    
    protected final void expect(ParseTree node, int childIndex, Class<? extends ParseTree> expectedType) {
        Class<? extends ParseTree> actualType = node.getChild(childIndex).getClass();
        if (!expectedType.isAssignableFrom(actualType)) {
            String lineInfo = "";
            if (node instanceof ParserRuleContext prc) {
                lineInfo = " at " + prc.getStart().getLine() + ":" + prc.getStart().getCharPositionInLine();
            }
            throw new UnexpectedTokenException(node, "Expected " + expectedType + "'; found " + actualType + lineInfo);
        }
    }
    
    protected final void expectEither(ParseTree node, int childIndex, 
            Class<? extends ParseTree> expectedType1, Class<? extends ParseTree> expectedType2) {
        Class<? extends ParseTree> actualType = node.getChild(childIndex).getClass();
        if (!expectedType1.isAssignableFrom(actualType) && !expectedType2.isAssignableFrom(actualType)) {
            String lineInfo = "";
            if (node instanceof ParserRuleContext prc) {
                lineInfo = " at " + prc.getStart().getLine() + ":" + prc.getStart().getCharPositionInLine();
            }
            throw new UnexpectedTokenException(node, "Expected " + expectedType1 + " or " + expectedType2 + "'; found " + actualType + lineInfo);
        }
    }

    protected final void expectText(ParseTree node, String expectedText) {
        String actualText = node.getText();
        if (!actualText.equals(expectedText)) {
            throw new UnexpectedTokenException(node, "Expected: '" + expectedText + "'");
        }
    }
    
    protected final void expectAnyText(ParseTree node, String... allowedTexts) {
        String actualText = node.getText();
        if (!Arrays.asList(allowedTexts).contains(actualText)) {
            throw new UnexpectedTokenException(node, "Expected any of: '" + Arrays.toString(allowedTexts) + "'");
        }
    }
    
    protected final SourceLocation loc(ParseTree node) {
        return SourceElement.locationOf(node);
    }
    
}
