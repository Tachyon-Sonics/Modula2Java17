package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

/**
 * SET OF [&lt;lower&gt;..&gt;upper&gt;]
 */
public class RangeSetType extends TypeBase implements IType, IByReferenceValueType, INamedType {

    private String typeName;
    private final IExpression lowerBound;
    private final IExpression upperBound;


    public RangeSetType(SourceLocation sLoc, IHasScope scopeUnit, String typeName,
            IExpression lowerBound, IExpression upperBound) {
        super(sLoc, scopeUnit);
        this.typeName = typeName;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * @return null if not named
     */
    public String getTypeName() {
        return typeName;
    }

    @Override
    public String getName() {
        return typeName;
    }

    @Override
    public void setName(String name) {
        this.typeName = name;
    }

    public IExpression getLowerBound() {
        return lowerBound;
    }
    
    public IExpression getUpperBound() {
        return upperBound;
    }

    @Override
    public String toString() {
        return "RangeSetType [lowerBound=" + lowerBound + ", upperBound=" + upperBound + "]";
    }

}
