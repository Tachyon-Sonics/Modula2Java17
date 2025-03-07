package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

/**
 * ARRAY with a single dimension. Multi-dimensional arrays are handled as ARRAY ... OF ARRAY...
 */
public class ArrayType extends TypeBase implements IType, IByReferenceValueType, IArrayType { // TODO handle "ARRAY <enum type> OF" or "ARRAY BOOLEAN OF"
    
    private final String name;
    private final IExpression lowerBound;
    private final IExpression upperBound;
    private final IType elementType;
    
    
    public ArrayType(SourceLocation sLoc, IHasScope scopeUnit, String name, IExpression lowerBound, IExpression upperBound, IType elementType) {
        super(sLoc, scopeUnit);
        this.name = name;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.elementType = elementType;
    }
    
    public String getName() {
        return name;
    }

    public IExpression getLowerBound() {
        return lowerBound;
    }
    
    public IExpression getUpperBound() {
        return upperBound;
    }
    
    @Override
    public IType getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return "ArrayType [name=" + name + ", lowerBound=" + lowerBound + ", upperBound=" + upperBound + ", elementType=" + elementType + "]";
    }

}
