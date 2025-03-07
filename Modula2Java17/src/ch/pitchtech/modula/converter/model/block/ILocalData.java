package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.model.INode;

/**
 * Common interface for all value holders: constants, variables and arguments
 */
public interface ILocalData extends INode {
    
    public String getName();
    
    public boolean isRead();
    
    public void setRead(boolean value);
    
    public boolean isWritten();
    
    public void setWritten(boolean value);
    
    /**
     * Whether the address is taken using the <tt>ADR</tt> function
     */
    public boolean isAddressed();
    
    public void setAddressed(boolean value);
    
    public default void copyAccessDataFrom(ILocalData other) {
        this.setRead(other.isRead());
        this.setWritten(other.isWritten());
        this.setAddressed(other.isAddressed());
    }
    

}
