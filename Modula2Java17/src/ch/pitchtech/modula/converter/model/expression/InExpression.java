package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class InExpression extends SourceElement implements IExpression {
    
    private final IExpression element;
    private final IExpression set;

    
    public InExpression(SourceLocation sLoc, IExpression element, IExpression set) {
        super(sLoc);
        this.element = element;
        this.set = set;
        attach(element, NodeAttachType.READ_ACCESS);
        attach(set, NodeAttachType.READ_ACCESS);
    }

    public IExpression getElement() {
        return element;
    }

    public IExpression getSet() {
        return set;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return element.isConstant(scope) && set.isConstant(scope);
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return new LiteralType(BuiltInType.BOOLEAN);
    }

    @Override
    public String toString() {
        return "InExpression [element=" + element + ", set=" + set + "]";
    }

}
