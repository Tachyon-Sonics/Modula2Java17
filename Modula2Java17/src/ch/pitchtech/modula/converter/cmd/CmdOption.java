package ch.pitchtech.modula.converter.cmd;

import java.util.List;
import java.util.Objects;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;

public abstract class CmdOption {

    private final String name;
    private final String fullName;
    private final String description;
    private final boolean hasValue;
    private final String argName;
    private final OptionType valueType;
    private final Class<? extends Enum<?>> enumType;


    public CmdOption(String name, String fullName, String description) {
        this(name, fullName, (OptionType) null, null, description);
    }

    public CmdOption(String name, String fullName, Class<? extends Enum<?>> enumType, String argName, String description) {
        this(name, fullName, OptionType.ENUM, argName, description, enumType);
    }

    public CmdOption(String name, String fullName, OptionType valueType, String argName, String description) {
        this(name, fullName, valueType, argName, description, null);
    }

    private CmdOption(String name, String fullName, OptionType valueType, String argName, String description,
            Class<? extends Enum<?>> enumType) {
        this.name = name;
        this.fullName = fullName;
        this.hasValue = (valueType != null);
        this.argName = argName;
        this.valueType = valueType;
        this.description = description;
        this.enumType = enumType;
    }

    public abstract void apply(Object value, FileOptions fileOptions, CompilerOptions compilerOptions);

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isHasValue() {
        return hasValue;
    }

    public String getArgName() {
        return argName;
    }

    public OptionType getValueType() {
        return valueType;
    }

    /**
     * Parse the given value.
     * @return the parse value, or an error message as a {@link String} if parsing failed.
     */
    public Object parseValue(String value) {
        if (valueType == OptionType.ENUM) {
            try {
                @SuppressWarnings({ "unchecked", "rawtypes" })
                Object result = OptionType.parseEnumValue((Class) enumType, value);
                return result;
            } catch (ParseEnumException ex) {
                return ex.getErrorMessage();
            }
        } else {
            return valueType.parseValue(value);
        }
    }
    
    public boolean isEnum() {
        return this.enumType != null;
    }

    public static CmdOption parse(List<CmdOption> options, String name) {
        for (CmdOption option : options) {
            if (option.getName().equals(name))
                return option;
        }
        return null;
    }

    public static CmdOption parseFull(List<CmdOption> options, String fullName) {
        for (CmdOption option : options) {
            if (option.getFullName().equals(fullName))
                return option;
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CmdOption other = (CmdOption) obj;
        return Objects.equals(fullName, other.fullName);
    }

}
