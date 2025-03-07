package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RecordType;


/**
 * "record.field"
 */
public class FieldAccess extends SourceElement implements IExpression {
    
    private final IExpression expression;
    private final Identifier field;

    
    public FieldAccess(SourceLocation sourceLocation, IExpression expression, Identifier field) {
        super(sourceLocation);
        this.expression = expression;
        this.field = field;
        attach(expression, NodeAttachType.READ_ACCESS);
        attach(field, NodeAttachType.INHERIT_ACCESS);
    }
    
    public IExpression getExpression() {
        return expression;
    }
    
    public Identifier getField() {
        return field;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        IType exprType = TypeResolver.resolveType(scope, expression.getType(scope));
        if (exprType instanceof RecordType recordType) {
            VariableDefinition recordItem = recordType.getExElements().stream().filter(
                    (vd) -> vd.getName().equals(field.getName()))
                    .findFirst().orElse(null);
            if (recordItem == null)
                throw new CompilationException(this, "Record '" + recordType.getName() + "' has no field '" + field.getName() + "'");
            return recordItem.getType();
        } else {
            throw new CompilationException(this, "Expression is not a record: " + expression);
        }
    }

    @Override
    public String toString() {
        return "FieldAccess [expression=" + expression + ", field=" + field + "]";
    }

}
