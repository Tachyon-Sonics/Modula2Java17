package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

// Example: myRecord.myProcVar(<args>)
public class ProcedureExpressionCall extends SourceElement implements IStatement, IMethodCall {
    // TODO FunctionExpressionCall

    private final IExpression procedureExpr;
    private final List<IExpression> arguments = new ArrayList<>();
    
    
    public ProcedureExpressionCall(SourceLocation sourceLocation, IExpression procedureExpr) {
        super(sourceLocation);
        this.procedureExpr = procedureExpr;
        attach(procedureExpr, NodeAttachType.READ_ACCESS);
    }
    
    public IExpression getProcedureExpr() {
        return procedureExpr;
    }

    @Override
    public List<IExpression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public void addArguments(List<IExpression> arguments) {
        this.arguments.addAll(arguments);
        attach(arguments, NodeAttachType.READ_ACCESS);
    }
    
    @Override
    public IDefinition resolveDefinition(IHasScope scopeUnit) {
        return null;
    }

    @Override
    public ProcedureType resolveType(IHasScope scopeUnit) {
        IType type = TypeResolver.resolveProcedureType(procedureExpr, scopeUnit);
        if (type instanceof ProcedureType procedureType)
            return procedureType;
        throw new CompilationException(this, "Expression is not of PROCEDURE type: {0}", procedureExpr);
    }

    @Override
    public String toString() {
        return "ProcedureExpressionCall [procedureExpr=" + procedureExpr + ", arguments=" + arguments + "]";
    }

}
