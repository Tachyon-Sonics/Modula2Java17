package ch.pitchtech.modula.converter.model.statement;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class Assignement extends SourceElement implements IStatement {
    
    private final IExpression target;
    private final IExpression value;
    
    
    public Assignement(SourceLocation sLoc, IExpression target, IExpression value) {
        super(sLoc);
        this.target = target;
        this.value = value;
        attach(target, NodeAttachType.WRITE_ACCESS);
        attach(value, NodeAttachType.READ_ACCESS);
    }
    
    public IExpression getTarget() {
        return target;
    }
    
    public IExpression getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Assignement [target=" + target + ", value=" + value + "]";
    }

}
