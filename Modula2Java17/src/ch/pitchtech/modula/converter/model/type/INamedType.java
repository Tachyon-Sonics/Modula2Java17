package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.block.IHasName;

public interface INamedType extends IType, IHasName {
    
    @Override
    public String getName();
    
    public void setName(String name);

}
