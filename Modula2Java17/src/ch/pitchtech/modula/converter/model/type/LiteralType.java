package ch.pitchtech.modula.converter.model.type;

import java.util.Objects;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class LiteralType extends TypeBase implements IType {
    
    private final String name;
    private final boolean builtIn;

    
    public LiteralType(SourceLocation sLoc, IHasScope scopeUnit, String name, boolean builtIn) {
        super(sLoc, scopeUnit);
        this.name = name;
        this.builtIn = builtIn;
    }
    
    public LiteralType(BuiltInType builtInType) {
        super(null, null);
        this.name = builtInType.name();
        this.builtIn = true;
    }

    public String getName() {
        return name;
    }

    public boolean isBuiltIn() {
        return builtIn;
    }

    @Override
    public boolean isBuiltInType(BuiltInType biType2) {
        if (isBuiltIn()) {
            BuiltInType biType1 = BuiltInType.valueOf(getName());
            return biType1 == biType2;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(builtIn, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LiteralType other = (LiteralType) obj;
        return builtIn == other.builtIn && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "LiteralType [name=" + name + "]";
    }

}
