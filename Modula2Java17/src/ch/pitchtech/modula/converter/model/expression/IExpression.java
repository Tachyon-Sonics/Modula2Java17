package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.type.IType;

public interface IExpression extends INode {
    
    public boolean isConstant(IScope scope);
    
    public IType getType(IScope scope, IType forType);
    
    public default IType getType(IScope scope) {
        return getType(scope, null);
    }
    
    /**
     * Whether the expression would need to be enclosed by parentheses if combined with another expression
     */
    public default boolean isComplex(ResultContext context) {
        return false;
    }
    
    /**
     * Try to evaluate the expression if it is constant
     */
    public default Object evaluateConstant() {
        return null;
    }
    
}
