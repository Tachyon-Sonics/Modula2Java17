package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.runtime.Runtime.IRef;

/**
 * Common interface for {@link VariableDefinition} and {@link FormalArgument}
 */
public interface ILocalVariable extends ILocalData, IHasName {

    public IType getType();

    public boolean isPassedAsVarAndWritten();

    public void setPassedAsVarAndWritten(boolean isPassedAsVarAndWritten);

    /**
     * Indicate that the variable or formal argument must be wrapped into an {@link IRef}.
     * <p>
     * This is necessary when a variable of a simple type (like integer) is passed by
     * VAR to a procedure, or is addressed with ADR
     */
    public boolean isUseRef();
    
    public void setUseRef(boolean useRef);
    
    /**
     * When non-null, this formal argument (or the corresponding {@link VariableDefinition})
     * does not exist in the Modula-2 code, but it has been generated and corresponds to an
     * argument or variable of the enclosing procedure.
     * <p>
     * Also used by a {@link VariableDefinition} that actually corresponds to a {@link FormalArgument}.
     */
    public ILocalVariable getSurrogateFor();

    public void setSurrogateFor(ILocalVariable surrogateFor);
    
    public default void copyAccessDataFrom(ILocalVariable other) {
        ILocalData.super.copyAccessDataFrom(other);
        this.setPassedAsVarAndWritten(other.isPassedAsVarAndWritten());
        this.setUseRef(other.isUseRef());
        this.setSurrogateFor(other.getSurrogateFor());
    }

}
