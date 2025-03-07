package ch.pitchtech.modula.converter.model.type;

import java.util.Objects;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class OpaqueType extends TypeBase implements IType { // TODO review everywhere PointerType is used whether OpaqueType should be handled too

    private final String name;

    
    public OpaqueType(SourceLocation sLoc, IHasScope scopeUnit, String name) {
        super(sLoc, scopeUnit);
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OpaqueType other = (OpaqueType) obj;
        return Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "OpaqueType [name=" + name + "]";
    }

}
