package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

public class ProcedureCall extends SourceElement implements IStatement, IMethodCall {

    private final IHasScope scopeUnit;
    private final String procedureName;
    private final List<IExpression> arguments = new ArrayList<>();
            
    
    public ProcedureCall(SourceLocation sLoc, IHasScope scopeUnit, String procedureName) {
        super(sLoc);
        this.scopeUnit = scopeUnit;
        this.procedureName = procedureName;
    }

    public String getProcedureName() {
        return procedureName;
    }
    
    @Override
    public List<IExpression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    public void addArguments(List<IExpression> arguments) {
        this.arguments.addAll(arguments);
        attach(arguments, NodeAttachType.READ_ACCESS);
    }
    
    public FunctionCall asFunction() {
        FunctionCall result = new FunctionCall(getSourceLocation(), scopeUnit, procedureName);
        result.addArguments(arguments);
        return result;
    }

    @Override
    public IDefinition resolveDefinition(IHasScope scopeUnit) {
        return scopeUnit.getScope().resolve(procedureName, false, false, true, true);
    }

    @Override
    public ProcedureType resolveType(IHasScope scopeUnit) {
        IDefinition definition = resolveDefinition(scopeUnit);
        if (definition instanceof ProcedureDefinition procedureDefinition)
            return new ProcedureType(procedureDefinition);
        else if (definition instanceof VariableDefinition variableDefinition) {
            IType type = variableDefinition.getType();
            if (type instanceof ProcedureType procedureType)
                return procedureType;
            else
                throw new CompilationException(this, "Not a procedure: {0}", procedureName);
        } else {
            throw new CompilationException(this, "Cannot resolve: {0}", procedureName);
        }
    }

    @Override
    public String toString() {
        return "ProcedureCall [procedureName=" + procedureName + ", arguments=" + arguments + "]";
    }

}
