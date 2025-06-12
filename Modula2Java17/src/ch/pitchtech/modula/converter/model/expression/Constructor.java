package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;

/**
 * Used internally for <tt>NEW</tt>. Generates Java <tt>new Xxx()</tt> expression.
 */
public class Constructor extends SourceElement implements IExpression {
    
    private final IType type;
    

    public Constructor(SourceLocation sourceLocation, IType type) {
        super(sourceLocation);
        this.type = type;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return type;
    }

}
