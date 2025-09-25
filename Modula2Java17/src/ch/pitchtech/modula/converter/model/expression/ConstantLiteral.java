package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

/**
 * Literal such as "0" or "FALSE"
 */
public class ConstantLiteral extends SourceElement implements IExpression {
    
    private final String value;

    
    public ConstantLiteral(SourceLocation sLoc, String value) {
        super(sLoc);
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return true;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        if (value.equals("TRUE") || value.equals("FALSE"))
            return new LiteralType(BuiltInType.BOOLEAN);
        if (value.startsWith("\"") || value.startsWith("'"))
            return new LiteralType(BuiltInType.STRING);
        if (value.contains("."))
            return new LiteralType(BuiltInType.LONGREAL);
        if (value.matches("\\d+C"))
            return new LiteralType(BuiltInType.CHAR);
        return new LiteralType(BuiltInType.getTypeForJavaInt());
    }

    @Override
    public Object evaluateConstant() {
        if (value.equals("true"))
            return true;
        else if (value.equals("false"))
            return false;
        else if (value.startsWith("\"") || value.startsWith("'"))
            return value.substring(1, value.length() - 1);
        if (value.contains(".")) {
            try {
                Double number = Double.parseDouble(value);
                return number;
            } catch (NumberFormatException ex) {
                // Ignore, not a real
            }
        }
        if (value.matches("\\d+C")) {
            try {
                short number = Short.parseShort(value, 8);
                return number;
            } catch (NumberFormatException ex) {
                // Ignore, not a number
            }
        } else if (value.matches("\\d+")) {
            try {
                Long number = Long.parseLong(value);
                return number;
            } catch (NumberFormatException ex) {
                // Ignore, not a number
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "ConstantLiteral [value=" + value + "]";
    }

}
