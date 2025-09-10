package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;

import org.antlr.v4.runtime.ParserRuleContext;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.source.CurrentFile;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceInfo;
import ch.pitchtech.modula.converter.utils.StringUtils;

/**
 * Models a compilation error that is the result of incorrect Modula-2 code
 */
public class CompilationException extends RuntimeException {
    
    private final Object element;

    
    /**
     * Create a compilation exception
     * @param element the element of the AST / Parse tree on which the error occured
     */
    public CompilationException(Object element, String message, Object... args) {
        super(StringUtils.format(message, args) + getLocationDetails(element));
        this.element = element;
    }

    public Object getElement() {
        return element;
    }
    
    static String getLocationDetails(Object element) {
        String result = "";
        SourceElement lastElement = Generator.getLastSourceElement();
        SourceInfo sourceInfo = null;
        if (lastElement != null) {
            result = lastElement.getLocationInfo();
            sourceInfo = lastElement.getSourceInfo();
        }
        
        if (element != lastElement) {
            if (element instanceof SourceElement se) {
                if (sameFileAndLine(se.getSourceInfo(), sourceInfo))
                    result = ""; // Replace by the following more accurate value
                if (!result.isEmpty())
                    result += " and ";
                result += se.getLocationInfo();
            } else if (element instanceof INode node) {
                if (!result.isEmpty())
                    result += "; path: ";
                while (node != null) {
                    if (result.length() > 0)
                        result = " > " + result;
                    result = node.toString() + result;
                    node = node.getParentNode();
                }
            } else if (element instanceof ParserRuleContext node) {
                Path currentFile = CurrentFile.getCurrentFile();
                if (currentFile != null) {
                    result = currentFile.toString();
                }
                result += " at " + node.getStart().getLine() + "." + node.getStart().getCharPositionInLine();
            }
        }
        if (!result.isEmpty())
            result = "; " + result;
        return result;
    }
    
    private static boolean sameFileAndLine(SourceInfo s1, SourceInfo s2) {
        if (s1 == null || s2 == null)
            return false;
        return s1.fileName().equals(s2.fileName()) && s1.location().startLine() == s2.location().startLine();
    }

}
