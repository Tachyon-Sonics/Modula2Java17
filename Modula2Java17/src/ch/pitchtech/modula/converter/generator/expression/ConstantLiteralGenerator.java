package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;

public class ConstantLiteralGenerator extends Generator {
    
    private final ConstantLiteral constantLiteral;

    
    public ConstantLiteralGenerator(IHasScope scopeUnit, ConstantLiteral constantLiteral) {
        super(scopeUnit, constantLiteral);
        this.constantLiteral = constantLiteral;
    }

    @Override
    public void generate(ResultContext result) {
        String value = constantLiteral.getValue();
        if (value.matches("\\d+C")) {
            // CHAR literal - octal
            String valueStr = value.substring(0, value.length() - 1);
            if (!valueStr.startsWith("0"))
                valueStr = "0" + valueStr;
            result.write("((char) " + valueStr + ")");
        } else if (value.matches("[0-9a-hA-H]+H")) {
            // Hexadecimal number
            if (value.startsWith("0") && !value.equals("0H"))
                value = value.substring(1);
            value = value.substring(0, value.length() - "H".length());
            result.write("0x" + value);
        } else {
            result.write(value);
        }
    }

}
