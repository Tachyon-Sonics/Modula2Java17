package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;

public class FormalArgument extends SourceElement implements ILocalVariable {
    
    private final boolean var;
    private final String name;
    private final IType type;
    
    private boolean isRead;
    private boolean isWritten;
    private boolean isPassedAsVarAndWritten;
    private boolean isAddressed;
    private boolean useRef;
    
    private ILocalVariable surrogateFor;
    
    
    public FormalArgument(SourceLocation sLoc, boolean var, String name, IType type) {
        super(sLoc);
        this.var = var;
        this.name = name;
        this.type = type;
    }

    public boolean isVar() {
        return var;
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
    public boolean isPassedAsVarAndWritten() {
        return isPassedAsVarAndWritten;
    }

    @Override
    public void setPassedAsVarAndWritten(boolean isPassedAsVarAndWritten) {
        this.isPassedAsVarAndWritten = isPassedAsVarAndWritten;
    }

    @Override
    public boolean isAddressed() {
        return isAddressed;
    }

    @Override
    public void setAddressed(boolean value) {
        isAddressed = value;
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
    public void setSurrogateFor(ILocalVariable surrogateFor) { // TODO seems never used
        this.surrogateFor = surrogateFor;
    }

    public VariableDefinition asVariableDefinition(IHasScope parent) { // TODO X implement in a way changing one changes the other
        VariableDefinition result = new VariableDefinition(getSourceLocation(), parent, name, type);
        result.copyAccessDataFrom(this);
        if (result.getSurrogateFor() == null)
            result.setSurrogateFor(this);
        return result;
    }
    
    @Override
    public String toString() {
        return "FormalArgument [var=" + var + ", name=" + name + ", type=" + type + "]";
    }

}
