package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.PointerType;

public class Dereference extends SourceElement implements IExpression {
    
    private final IExpression pointer;

    
    public Dereference(SourceLocation sLoc, IExpression pointer) {
        super(sLoc);
        this.pointer = pointer;
        attach(pointer, NodeAttachType.READ_ACCESS);
    }

    public IExpression getPointer() {
        return pointer;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        IType type = TypeResolver.resolveType(scope, pointer.getType(scope));
        if (type instanceof PointerType pointerType)
            return TypeResolver.resolveType(scope, pointerType.getTargetType());
        else
            throw new CompilationException(this, "Dereferenced item is not a pointer: " + pointer + "; type " + type);
    }

    @Override
    public String toString() {
        return "Dereference [pointer=" + pointer + "]";
    }

}
