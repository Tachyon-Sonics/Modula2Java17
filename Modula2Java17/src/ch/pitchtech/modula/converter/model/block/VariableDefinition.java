package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.runtime.Runtime.Ref;

public class VariableDefinition extends SourceElement implements INode, IDefinition, ILocalVariable {
    
    private final IHasScope parent;
    private final String name;
    private final IType type;
    
    // TODO convert to Boolean (null=unknown) when applicable for incremental compilation
    private boolean isRead;
    private boolean isWritten;
    private boolean isAddressed;
    private boolean isPassedAsVarAndWritten;
    private boolean useRef;
    
    private ILocalVariable surrogateFor;
    
    
    public VariableDefinition(SourceLocation sLoc, IHasScope parent, String name, IType type) {
        super(sLoc);
        this.parent = parent;
        this.name = name;
        this.type = type;
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
    
    @Override
    public IType getType() {
        return type;
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
        return isWritten;
    }

    @Override
    public void setWritten(boolean isWritten) {
        this.isWritten = isWritten;
    }

    @Override
    public boolean isAddressed() {
        return isAddressed;
    }

    @Override
    public void setAddressed(boolean value) {
        isAddressed = value;
    }
    
    /**
     * Whether this variable is passed at least once as a VAR argument that is actually modified (written) by the
     * underlying procedure.
     */
    @Override
    public boolean isPassedAsVarAndWritten() {
        return isPassedAsVarAndWritten;
    }

    @Override
    public void setPassedAsVarAndWritten(boolean isPassedAsVarAndWritten) {
        this.isPassedAsVarAndWritten = isPassedAsVarAndWritten;
    }

    @Override
    public boolean isUseRef() {
        return useRef;
    }
    
    @Override
    public void setUseRef(boolean useRef) {
        this.useRef = useRef;
    }
    
    @Override
    public ILocalVariable getSurrogateFor() {
        return surrogateFor;
    }
    
    @Override
    public void setSurrogateFor(ILocalVariable surrogateFor) {
        this.surrogateFor = surrogateFor;
    }

    /**
     * If this variable is stored in a {@link Ref}, as given by {@link #isUseRef()},
     * execute the given code with {@link #isUseRef()} temporarily set to false. Typically
     * used to generate the {@link Ref} itself rather than the {@link Ref}'s value.
     * @throws IllegalStateException if {@link #isUseRef()} is <tt>false</tt>.
     */
    public void asReference(Runnable execute) {
        if (!useRef)
            throw new IllegalStateException();
        setUseRefDeep(this, false);
        try {
            execute.run();
        } finally {
            setUseRefDeep(this, true);
        }
    }
    
    private static void setUseRefDeep(ILocalVariable localVariable, boolean useRef) {
        while (localVariable != null) {
            localVariable.setUseRef(useRef);
            localVariable = localVariable.getSurrogateFor();
        }
    }

    @Override
    public String toString() {
        return "VariableDefinition [name=" + name + ", type=" + type + "]";
    }

}
