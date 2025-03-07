package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;

/**
 * {@link Identifier} corresponding to an enumeration item.
 */
public class EnumItemIdentifier extends Identifier {
    
    private final EnumerationType type;
    

    public EnumItemIdentifier(SourceLocation sLoc, IHasScope scopeUnit, String name, EnumerationType enumerationType) {
        super(sLoc, scopeUnit, name);
        this.type = enumerationType;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return true;
    }

    @Override
    public IType getType(IScope scope) {
        return this.type;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return this.type;
    }

}
