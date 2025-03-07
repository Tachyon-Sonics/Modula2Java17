package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class PointerType extends TypeBase implements IType {
    // TODO clarify the name when: VAR x: POINTER TO YY;
    
    private final String name;
    private final IType targetType;

    
    public PointerType(SourceLocation sLoc, IHasScope scopeUnit, String name, IType targetType) {
        super(sLoc, scopeUnit);
        this.name = name;
        this.targetType = targetType;
    }
    
    public String getName() {
        return name;
    }

    public IType getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "PointerType [name=" + name + ", targetType=" + targetType + "]";
    }

}
