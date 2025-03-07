package ch.pitchtech.modula.converter.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.model.source.CurrentFile;

public class UnexpectedTokenException extends RuntimeException {
    
    public UnexpectedTokenException(ParseTree node) {
        super("Unexpected token " + nodeToString(node) + getLineInfo(node));
    }
    
    public UnexpectedTokenException(ParseTree node, String message) {
        super("Unexpected token " + nodeToString(node) + getLineInfo(node) + ": " + message);
    }
    
    public UnexpectedTokenException(String message, ParseTree node) {
        super(message + "\n at token " + nodeToString(node) + getLineInfo(node));
    }
    
    private static String nodeToString(ParseTree node) {
        if (node == null)
            return "<null>";
        if (node instanceof TerminalNode)
            return "\"" + node.getText() + "\"";
        else
            return "type " + node.getClass().getSimpleName();
    }
    
    private static String getLineInfo(ParseTree node) {
        String lineInfo = "";
        if (CurrentFile.getCurrentFile() != null) {
            lineInfo = "; file " + CurrentFile.getCurrentFile().toString();
        }
        while (node.getParent() != null && !(node instanceof ParserRuleContext))
            node = node.getParent();
        if (node instanceof ParserRuleContext prc) {
            int startLine = prc.getStart().getLine();
            int stopLine = prc.getStop().getLine();
            if (startLine >= stopLine)
                lineInfo += " at " + startLine + ":" + prc.getStart().getCharPositionInLine();
            else
                lineInfo += " from line " + startLine + " to " + stopLine;
        }
        return lineInfo;
    }

}
