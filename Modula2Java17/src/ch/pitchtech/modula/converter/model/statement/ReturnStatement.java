package ch.pitchtech.modula.converter.model.statement;

import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;


public class ReturnStatement extends SourceElement implements IStatement {
    
    private final IExpression expression; // Can be null
    private final ProcedureImplementation procedureImplementation; // Containing procedure

    
    public ReturnStatement(SourceLocation sourceLocation, ProcedureImplementation procedureImplementation, IExpression expression) {
        super(sourceLocation);
        this.procedureImplementation = procedureImplementation;
        this.expression = expression;
        if (expression != null)
            attach(expression, NodeAttachType.READ_ACCESS);
    }
    
    public ReturnStatement(SourceLocation sourceLocation, ProcedureImplementation procedureImplementation) {
        this(sourceLocation, procedureImplementation, null);
    }
    
    public IExpression getExpression() {
        return expression;
    }
    
    public ProcedureImplementation getProcedureImplementation() {
        return procedureImplementation;
    }

    @Override
    public String toString() {
        return "ReturnStatement [expression=" + expression + "]";
    }

}
