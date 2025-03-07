package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

/**
 * "-&lt;expr&gt;", such as "-4"
 */
public class MinusExpression extends SourceElement implements IExpression {
    
    private final IExpression target;

    
    public MinusExpression(SourceLocation sLoc, IExpression target) {
        super(sLoc);
        this.target = target;
        attach(target, NodeAttachType.READ_ACCESS);
    }
    
    public IExpression getTarget() {
        return target;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return target.isConstant(scope);
    }

    @Override
    public Object evaluateConstant() {
        Object result = target.evaluateConstant();
        if (result == null)
            return null;
        if (result instanceof Double number)
            return -number.doubleValue();
        else if (result instanceof Long number)
            return -number.longValue();
        return null;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        IType type = TypeResolver.resolveType(scope, target.getType(scope, forType));
        if (type instanceof LiteralType literalType && literalType.isBuiltIn()) {
            BuiltInType biType = BuiltInType.valueOf(literalType.getName());
            if (biType.isNumeric() && !biType.isDecimal()) {
                if (biType.getSize() < BuiltInType.javaInt().getSize())
                    return new LiteralType(BuiltInType.javaInt()); // Java promotes to at least "int"
            }
        }
        return type;
    }

    @Override
    public String toString() {
        return "MinusExpression [target=" + target + "]";
    }

}
