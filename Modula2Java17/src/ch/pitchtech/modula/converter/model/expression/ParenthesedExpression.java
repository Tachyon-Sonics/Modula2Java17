package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;


public class ParenthesedExpression extends SourceElement implements IExpression {
    
    private final IExpression target;

    
    public ParenthesedExpression(SourceLocation sourceLocation, IExpression target) {
        super(sourceLocation);
        this.target = target;
        attach(target, NodeAttachType.READ_ACCESS);
    }
    
    public IExpression getTarget() {
        return target;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return target.isConstant(scope);
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return target.getType(scope, forType);
    }
    
    @Override
    public String toString() {
        return "(" + target.toString() + ")";
    }

}
