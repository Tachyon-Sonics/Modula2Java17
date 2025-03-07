package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class TypeDefinition extends SourceElement implements INode, IDefinition {
    
    private final IHasScope parent;
    private String name;
    private final IType type;
    
    
    public TypeDefinition(SourceLocation sLoc, IHasScope parent, String name, IType type) {
        super(sLoc);
        this.parent = parent;
        this.name = name;
        this.type = type;
    }
    
    /**
     * Create an opaque type
     */
    public TypeDefinition(SourceLocation sLoc, IHasScope parent, String name) {
        this(sLoc, parent, name, null);
    }
    
    @Override
    public IHasScope getParent() {
        return parent;
    }

    @Override
    public INode getParentNode() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public void setNewName(String newName) {
        super.setNewName(newName);
        if (type instanceof SourceElement element) {
            element.setNewName(newName);
        }
    }

    public IType getType() {
        return type;
    }
    
    public boolean isOpaque() {
        return (type == null);
    }

    @Override
    public String toString() {
        return "TypeDefinition [name=" + name + ", type=" + type + "]";
    }

}
