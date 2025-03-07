package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;


public class StringLiteralGenerator extends Generator {
    
    private final StringLiteral stringLiteral;

    
    public StringLiteralGenerator(IHasScope scopeUnit, StringLiteral stringLiteral) {
        super(scopeUnit, stringLiteral);
        this.stringLiteral = stringLiteral;
    }

    @Override
    public void generate(ResultContext result) {
        result.write("\"");
        result.write(stringLiteral.getText().replace("\\", "\\\\").replace("\"", "\\\""));
        result.write("\"");
        if (!result.getCompilerOptions().isConvertArrayOfCharToString())
            result.write(".toCharArray()");
    }
    
    public void generateAsChar(ResultContext result) {
        result.write("'");
        result.write(stringLiteral.getText().replace("\\", "\\\\").replace("'", "\\'"));
        result.write("'");
    }

}
