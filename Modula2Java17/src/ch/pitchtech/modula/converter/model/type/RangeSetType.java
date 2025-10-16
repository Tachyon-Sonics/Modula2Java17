package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

/**
 * SET OF [&lt;lower&gt;..&gt;upper&gt;]
 */
public class RangeSetType extends TypeBase implements IType, IByReferenceValueType {
    
    private final String typeName;
    private final boolean inline;
    private final IExpression lowerBound;
    private final IExpression upperBound;
    
    
    /**
     * @param inline whether the type is used inline, like "SET OF [0..15]" instead of "MySetType". In that case
     * <tt>typeName</tt> is synthetic.
     */
    public RangeSetType(SourceLocation sLoc, IHasScope scopeUnit, String typeName, boolean inline,
            IExpression lowerBound, IExpression upperBound) {
        super(sLoc, scopeUnit);
        this.typeName = typeName;
        this.inline = inline;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    /**
     * @return null if not named
     */
    public String getTypeName() {
        return typeName;
    }
    
    public boolean isInline() {
        return inline;
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
