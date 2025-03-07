package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.FieldAccess;
import ch.pitchtech.modula.converter.model.scope.IHasScope;


public class FieldAccessGenerator extends Generator {
    
    private final FieldAccess fieldAccess;

    
    public FieldAccessGenerator(IHasScope scopeUnit, FieldAccess fieldAccess) {
        super(scopeUnit, fieldAccess);
        this.fieldAccess = fieldAccess;
    }

    @Override
    public void generate(ResultContext result) {
        Expressions.getGenerator(scopeUnit, fieldAccess.getExpression()).generate(result);
        result.write(".");
        result.write(fieldAccess.getField().getName());
    }

}
