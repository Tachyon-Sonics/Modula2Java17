package ch.pitchtech.modula.converter.model.statement;

import java.util.List;

import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

/**
 * Common interface for all calls: {@link ProcedureCall}, {@link FunctionCall}, {@link ProcedureExpressionCall}
 */
public interface IMethodCall extends INode {
    
    public List<IExpression> getArguments();
    
    /**
     * May return null for an expression such as "myRecord.myProcItem(...)"
     */
    public IDefinition resolveDefinition(IHasScope scopeUnit);
    
    public ProcedureType resolveType(IHasScope scopeUnit);

}
