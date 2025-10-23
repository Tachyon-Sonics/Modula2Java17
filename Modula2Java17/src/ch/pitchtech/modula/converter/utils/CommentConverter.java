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
     * <li>Preserve nested (* and *) as they don't conflict with Java syntax</li>
     * <li>Convert to Java block comment format</li>
     * <li>Normalize indentation by removing common leading whitespace</li>
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

        // Nested (* and *) delimiters are kept as-is since they don't conflict
        // with Java's /* */ comment syntax

        // Normalize indentation: remove common leading whitespace from all lines
        content = normalizeIndentation(content);

        // Build Java comment with proper formatting
        return "/*" + content + "*/";
    }

    /**
     * Normalize indentation by finding and removing common leading whitespace
     * from all non-empty lines.
     *
     * @param text the text to normalize
     * @return the text with normalized indentation
     */
    private static String normalizeIndentation(String text) {
        String[] lines = text.split("\n", -1);
        if (lines.length <= 1) {
            return text;
        }

        // Find minimum indentation (ignoring empty lines)
        int minIndent = Integer.MAX_VALUE;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                int indent = 0;
                while (indent < line.length() && line.charAt(indent) == ' ') {
                    indent++;
                }
                minIndent = Math.min(minIndent, indent);
            }
        }

        // Remove common indentation from all lines
        if (minIndent > 0 && minIndent < Integer.MAX_VALUE) {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.trim().isEmpty()) {
                    result.append(line);
                } else if (line.length() > minIndent) {
                    result.append(line.substring(minIndent));
                } else {
                    result.append(line);
                }
                if (i < lines.length - 1) {
                    result.append("\n");
                }
            }
            return result.toString();
        }

        return text;
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

        // Nested (* and *) delimiters are kept as-is since they don't conflict
        // with Java's /* */ comment syntax

        // Normalize indentation
        content = normalizeIndentation(content);

        // Build Javadoc comment
        return "/**" + content + "*/";
    }
}
