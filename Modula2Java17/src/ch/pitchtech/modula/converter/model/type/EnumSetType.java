package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

/**
 * "SET OF xxx", where "xxx" is an enum type ({@link EnumerationType})
 */
public class EnumSetType extends TypeBase implements IType, IByReferenceValueType {
    
    private final String name;
    private final LiteralType enumerationType;

    
    public EnumSetType(SourceLocation sLoc, IHasScope scopeUnit, String name, LiteralType enumerationType) {
        super(sLoc, scopeUnit);
        this.name = name;
        this.enumerationType = enumerationType;
    }
    
    public String getName() {
        return name;
    }

    public LiteralType getEnumerationType() {
        return enumerationType;
    }

    @Override
    public String toString() {
        return "EnumSetType [name=" + name + ", enumerationType=" + enumerationType + "]";
    }

}
