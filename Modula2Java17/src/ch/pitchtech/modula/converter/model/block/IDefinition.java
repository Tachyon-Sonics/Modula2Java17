package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.model.scope.IHasScope;

/**
 * Common interface for items that can be exported/imported from a definition module: types, constants, variables
 * and procedures.
 */
public interface IDefinition extends IHasName {
    
    @Override
    public String getName();
    
    public IHasScope getParent();

}
