package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;

public interface IType {
    
    public IHasScope getDeclaringScope();
    
    public default ICompilationUnit getDeclaringUnit() {
        return getDeclaringScope().getCompilationUnit();
    }
    
    /**
     * Whether this type is the given built-in (or SYSTEM) type
     */
    public default boolean isBuiltInType(BuiltInType biType) {
        return false;
    }

}
