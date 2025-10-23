package ch.pitchtech.modula.converter.utils;

/**
 * Utility class for converting Modula-2 comments to Java comment syntax.
 * <p>
 * Modula-2 comments use (* ... *) syntax (potentially nested).
 * Java comments use block comment and Javadoc formats.
 */
public class CommentConverter {

    /**
     * Converts a Modula-2 comment to Java comment syntax.
     * <p>
     * Conversion rules:
     * <ul>
     * <li>Remove outer (* and *) delimiters</li>
     * <li>Replace nested (* and *) to avoid syntax errors</li>
     * <li>Convert to Java block comment format</li>
     * <li>Preserve formatting and whitespace</li>
     * </ul>
     *
     * @param modula2Comment the Modula-2 comment text (including (* and *) delimiters)
     * @return the converted Java comment
     */
    public static String convertToJavaComment(String modula2Comment) {
        if (modula2Comment == null || modula2Comment.isEmpty()) {
            return "";
        }

        // Remove outer (* and *)
        String content = modula2Comment.trim();
        if (content.startsWith("(*") && content.endsWith("*)")) {
            content = content.substring(2, content.length() - 2);
        }

        // Replace nested comment delimiters to avoid Java syntax errors
        // Replace (* with /* and *) with */
        content = content.replace("(*", "/*");
        content = content.replace("*)", "*/");

        // Build Java comment with proper formatting
        return "/*" + content + "*/";
    }

    /**
     * Converts a Modula-2 comment to a Java single-line comment.
     * Useful for short comments that fit on one line.
     *
     * @param modula2Comment the Modula-2 comment text
     * @return the converted Java single-line comment
     */
    public static String convertToJavaSingleLineComment(String modula2Comment) {
        if (modula2Comment == null || modula2Comment.isEmpty()) {
            return "";
        }

        // Remove outer (* and *)
        String content = modula2Comment.trim();
        if (content.startsWith("(*") && content.endsWith("*)")) {
            content = content.substring(2, content.length() - 2).trim();
        }

        // If content contains newlines, use block comment instead
        if (content.contains("\n")) {
            return convertToJavaComment(modula2Comment);
        }

        // Build single-line comment
        return "// " + content;
    }

    /**
     * Determines if a comment should be converted to Javadoc format.
     * Heuristics include checking if the comment appears before a declaration
     * or contains Javadoc-like annotations.
     *
     * @param modula2Comment the Modula-2 comment text
     * @return true if the comment should be converted to Javadoc
     */
    public static boolean shouldBeJavadoc(String modula2Comment) {
        if (modula2Comment == null) {
            return false;
        }

        // Simple heuristic: comments with @param, @return, etc. should be Javadoc
        return modula2Comment.contains("@param") ||
               modula2Comment.contains("@return") ||
               modula2Comment.contains("@author") ||
               modula2Comment.contains("@see");
    }

    /**
     * Converts a Modula-2 comment to Javadoc format.
     *
     * @param modula2Comment the Modula-2 comment text
     * @return the converted Javadoc comment
     */
    public static String convertToJavadoc(String modula2Comment) {
        if (modula2Comment == null || modula2Comment.isEmpty()) {
            return "";
        }

        // Remove outer (* and *)
        String content = modula2Comment.trim();
        if (content.startsWith("(*") && content.endsWith("*)")) {
            content = content.substring(2, content.length() - 2);
        }

        // Replace nested comment delimiters
        content = content.replace("(*", "/*");
        content = content.replace("*)", "*/");

        // Build Javadoc comment
        return "/**" + content + "*/";
    }
}
