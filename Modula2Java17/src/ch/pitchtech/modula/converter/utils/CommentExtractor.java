package ch.pitchtech.modula.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Lexer;
import ch.pitchtech.modula.converter.model.Comment;

/**
 * Utility class for extracting Modula-2 comments from ANTLR token streams.
 * Comments are sent to the HIDDEN channel during lexing and can be retrieved
 * for conversion to Java comment syntax.
 */
public class CommentExtractor {

    /**
     * Extracts all comments from the given token stream.
     * Comments are found on the HIDDEN channel.
     *
     * @param tokenStream the token stream containing the parsed Modula-2 source
     * @return list of comments in source order
     */
    public static List<Comment> extractComments(CommonTokenStream tokenStream) {
        List<Comment> comments = new ArrayList<>();

        if (tokenStream == null) {
            return comments;
        }

        // Get all tokens including those on hidden channels
        tokenStream.fill();
        List<Token> tokens = tokenStream.getTokens();

        for (Token token : tokens) {
            // Check if this is a COMMENT token on the HIDDEN channel
            if (token.getType() == m2pim4Lexer.COMMENT) {
                String text = token.getText();
                int line = token.getLine();
                int column = token.getCharPositionInLine();
                comments.add(new Comment(text, line, column));
            }
        }

        return comments;
    }

}
