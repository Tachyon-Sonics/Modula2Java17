package ch.pitchtech.modula.converter.model.type;


import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public abstract class TypeBase extends SourceElement implements IType {
    
    private final IHasScope declaringScope;

    
    public TypeBase(SourceLocation sLoc, IHasScope declaringScope) {
        super(sLoc);
        this.declaringScope = declaringScope;
    }

    @Override
    public IHasScope getDeclaringScope() {
        return declaringScope;
    }

}
