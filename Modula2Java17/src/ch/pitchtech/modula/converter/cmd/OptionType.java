package ch.pitchtech.modula.converter.cmd;

import java.nio.file.Path;

public enum OptionType {
    STRING(String.class) {
        @Override
        public Object parseValue(String value) {
            return value;
        }
    },
    DOUBLE(Double.class) {
        @Override
        public Object parseValue(String value) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException ex) {
                return "'" + value + "' is not a number";
            }
        }
    },
    FILE(Path.class) {
        @Override
        public Object parseValue(String value) {
            return Path.of(value);
        }
    },
    DIRECTORY(Path.class) {
        @Override
        public Object parseValue(String value) {
            return Path.of(value);
        }
    },
    BOOLEAN(Boolean.class) {

        @Override
        public Object parseValue(String value) {
            if (value.toLowerCase().equals("on") || value.toLowerCase().equals("true"))
                return true;
            if (value.toLowerCase().equals("off") || value.toLowerCase().equals("false"))
                return false;
            return "'" + value + "' is not 'on' or 'off' (or 'true' or 'false')";
        }
    },
    INTEGER(Integer.class) {
        @Override
        public Object parseValue(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return "'" + value + "' is not an integer number";
            }
        }
    },
    ENUM(Enum.class) {
        @Override
        public Object parseValue(String value) {
            throw new IllegalStateException("Cannot parse enum without a list of values. Use OptionType.parseEnumValue() instead.");
        }
    };


    private final Class<?> type;


    private OptionType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    /**
     * Parse the given value and return the result, or an error string
     */
    public abstract Object parseValue(String value);

    /**
     * Smart parse an enum value.
     * <p>
     * Pasing is case insensitive, and only a prefix of the enum value can be given, as long
     * as it is not ambiguous.
     * @throws ParseEnumException if the value does not match any enum item, or if it is ambiguous
     */
    public static <E extends Enum<E>> E parseEnumValue(Class<E> enumClass, String value) throws ParseEnumException {
        E result = null;
        for (E enumValue : enumClass.getEnumConstants()) {
            if (enumValue.name().toLowerCase().startsWith(value.toLowerCase())) {
                if (result != null) {
                    // Report ambiguous value
                    String error = "'" + value + "' is ambiguous; it can be: ";
                    boolean isFirst = true;
                    for (E enumValue0 : enumClass.getEnumConstants()) {
                        if (enumValue0.name().toLowerCase().startsWith(value.toLowerCase())) {
                            if (!isFirst)
                                error += ", ";
                            isFirst = false;
                            error += enumValue0.name();
                        }
                    }
                    throw new ParseEnumException(enumClass, value, error);
                } else {
                    result = enumValue;
                }
            }
        }
        if (result == null) {
            // Report error
            String error = "'" + value + "' is not one of ";
            boolean isFirst = true;
            for (E enumValue : enumClass.getEnumConstants()) {
                if (!isFirst)
                    error += ", ";
                isFirst = false;
                error += enumValue.name().toLowerCase();
            }
            throw new ParseEnumException(enumClass, value, error);
        }
        return result;
    }

}
