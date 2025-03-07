package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class OpenArrayType extends TypeBase implements IType, IByReferenceValueType, IArrayType {
    
    private final LiteralType elementType;

    
    public OpenArrayType(SourceLocation sLoc, IHasScope scopeUnit, LiteralType elementType) {
        super(sLoc, scopeUnit);
        this.elementType = elementType;
    }
    
    @Override
    public LiteralType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return "OpenArrayType [elementType=" + elementType + "]";
    }

}
