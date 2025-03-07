package ch.pitchtech.modula.converter.model.type;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

/**
 * Pseudo pointer type used whenever the ADR(...) expression is encountered
 */
public class AdrPointerType extends PointerType {
    
    private final IExpression addressedExpression;
    

    public AdrPointerType(SourceLocation sLoc, IHasScope scopeUnit, IExpression addressedExpression, IType targetType) {
        super(sLoc, scopeUnit, "<ADR>", targetType);
        this.addressedExpression = addressedExpression;
    }

    public IExpression getAddressedExpression() {
        return addressedExpression;
    }

}
