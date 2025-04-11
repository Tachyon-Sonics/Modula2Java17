package ch.pitchtech.modula.converter.cmd;

public class ParseEnumException extends Exception {

    private final Class<? extends Enum<?>> enumClass;
    private final String value;
    private final String errorMessage;


    public ParseEnumException(Class<? extends Enum<?>> enumClass, String value, String errorMessage) {
        super("Cannot parse \"" + value + "\" as " + enumClass.getName() + ": " + errorMessage);
        this.enumClass = enumClass;
        this.value = value;
        this.errorMessage = errorMessage;
    }

    public Class<? extends Enum<?>> getEnumClass() {
        return enumClass;
    }

    public String getValue() {
        return value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
