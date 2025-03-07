package ch.pitchtech.modula.converter;

import java.util.Collection;

// xBR Java: https://github.com/stanio/xbrz-java
public class StringUtils {
    
    public static String toCamelCase(String text) {
        return text.substring(0, 1).toLowerCase() + text.substring(1);
    }
    
    public static String toPascalCase(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    public static String toSnakeCase(String text) {
        StringBuilder result = new StringBuilder();
        boolean upper = true;
        for (char ch : text.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                if (!upper)
                    result.append("_");
                upper = true;
            } else {
                upper = false;
            }
            result.append(Character.toLowerCase(ch));
        }
        return result.toString();
    }
    
    public static String toUpperSnakeCase(String text) {
        return toSnakeCase(text).toUpperCase();
    }
    
    public static String toString(Collection<String> items) {
        StringBuilder result = new StringBuilder();
        for (String item : items) {
            if (result.length() > 0)
                result.append(", ");
            result.append(item);
        }
        return result.toString();
    }

    public static String format(String text, Object... args) {
        for (int i = 0; i < args.length; i++)
            text = text.replace("{" + i + "}", String.valueOf(args[i]));
        return text;
    }

}
