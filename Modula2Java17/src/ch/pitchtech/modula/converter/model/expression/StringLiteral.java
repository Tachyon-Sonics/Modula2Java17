package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;


public class StringLiteral extends SourceElement implements IExpression {
    
    private final String text;

    
    public StringLiteral(SourceLocation sourceLocation, String text) {
        super(sourceLocation);
        this.text = text;
    }
    
    public String getText() {
        return text;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return true;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return new LiteralType(BuiltInType.STRING);
    }

    @Override
    public String toString() {
        return "StringLiteral [text=" + text + "]";
    }

}
