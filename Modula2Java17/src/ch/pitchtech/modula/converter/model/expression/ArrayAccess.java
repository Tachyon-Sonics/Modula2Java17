package ch.pitchtech.modula.converter.model.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;

public class ArrayAccess extends SourceElement implements IExpression {
    
    private final IExpression array;
    private final List<IExpression> indexes = new ArrayList<>();

    
    public ArrayAccess(SourceLocation sLoc, IExpression array) {
        super(sLoc);
        this.array = array;
        attach(array, NodeAttachType.INHERIT_ACCESS);
    }

    public IExpression getArray() {
        return array;
    }
    
    public List<IExpression> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }
    
    public void addIndexes(List<IExpression> indexes) {
        this.indexes.addAll(indexes);
        attach(indexes, NodeAttachType.READ_ACCESS);
    }

    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        IType type = TypeResolver.resolveType(scope, array.getType(scope));
        for (int k = 0; k < indexes.size(); k++) {
            if (type instanceof IArrayType arrayType) {
                type = TypeResolver.resolveType(scope, arrayType.getElementType());
            } else {
                throw new CompilationException(this, "Expression '{0}' is not of array type (or there are too many indexes): {1}", this, type);
            }
        }
        return type;
    }

    @Override
    public String toString() {
        return "ArrayAccess [array=" + array + ", indexes=" + indexes + "]";
    }

}
