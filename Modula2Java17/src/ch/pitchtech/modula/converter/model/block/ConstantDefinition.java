package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class ConstantDefinition extends SourceElement implements INode, IDefinition, ILocalData {
    
    private final IHasScope parent;
    private final String name;
    private final IExpression value;
    
    private boolean isRead;
    private boolean isAddressed;

    
    public ConstantDefinition(SourceLocation sLoc, IHasScope parent, String name, IExpression value) {
        super(sLoc);
        this.parent = parent;
        this.name = name;
        this.value = value;
        attach(value, NodeAttachType.READ_ACCESS);
    }
    
    @Override
    public IHasScope getParent() {
        return parent;
    }

    @Override
    public INode getParentNode() {
        return (INode) parent;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public IExpression getValue() {
        return value;
    }
    
    @Override
    public boolean isRead() {
        return isRead;
    }

    @Override
    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public boolean isWritten() {
        return false;
    }

    @Override
    public void setWritten(boolean value) {
        throw new CompilerException(this, "Attempt to write constant: " + this);
    }

    @Override
    public String toString() {
        return "ConstantDefinition [name=" + name + ", value=" + value + "]";
    }

    @Override
    public boolean isAddressed() {
        return isAddressed;
    }

    @Override
    public void setAddressed(boolean value) {
        isAddressed = value;
    }

}
