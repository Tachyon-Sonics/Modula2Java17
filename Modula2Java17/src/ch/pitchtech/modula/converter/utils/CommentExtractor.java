package ch.pitchtech.modula.converter.utils;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Lexer;

/**
 * Utility class for extracting Modula-2 comments from ANTLR token streams.
 * Comments are sent to the HIDDEN channel during lexing and can be retrieved
 * for conversion to Java comment syntax.
 */
public class CommentExtractor {

    /**
     * Represents a comment extracted from the source code.
     */
    public static class Comment {
        private final String text;
        private final int line;
        private final int column;

        public Comment(String text, int line, int column) {
            this.text = text;
            this.line = line;
            this.column = column;
        }

        public String getText() {
            return text;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }

        @Override
        public String toString() {
            return "Comment[line=" + line + ", col=" + column + ", text=" + text + "]";
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Comment other = (Comment) obj;
            return line == other.line &&
                   column == other.column &&
                   text.equals(other.text);
        }

        @Override
        public int hashCode() {
            int result = text.hashCode();
            result = 31 * result + line;
            result = 31 * result + column;
            return result;
        }
    }

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

    /**
     * Gets all comments that appear before a given line number.
     * Useful for extracting comments that document a declaration.
     *
     * @param tokenStream the token stream
     * @param beforeLine the line number (exclusive)
     * @return list of comments before the given line
     */
    public static List<Comment> getCommentsBefore(CommonTokenStream tokenStream, int beforeLine) {
        List<Comment> allComments = extractComments(tokenStream);
        List<Comment> result = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getLine() < beforeLine) {
                result.add(comment);
            } else {
                break; // Comments are in source order
            }
        }

        return result;
    }

    /**
     * Gets all comments that appear on or between two line numbers.
     *
     * @param tokenStream the token stream
     * @param startLine the start line (inclusive)
     * @param endLine the end line (inclusive)
     * @return list of comments in the given range
     */
    public static List<Comment> getCommentsBetween(CommonTokenStream tokenStream, int startLine, int endLine) {
        List<Comment> allComments = extractComments(tokenStream);
        List<Comment> result = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getLine() >= startLine && comment.getLine() <= endLine) {
                result.add(comment);
            }
        }

        return result;
    }
}
