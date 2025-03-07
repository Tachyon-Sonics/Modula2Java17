package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class ElsIfStatement extends SourceElement implements IStatementsContainer {

    private final IExpression condition;
    private List<IStatement> statements = new ArrayList<>();
    
    
    public ElsIfStatement(SourceLocation sLoc, IExpression condition, List<IStatement> instructions) {
        super(sLoc);
        this.condition = condition;
        this.statements = instructions;
        attach(condition, NodeAttachType.READ_ACCESS);
        attach(instructions, NodeAttachType.DEFAULT);
    }
    
    public List<IStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }
    
    public IExpression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "ElseIfStatement [condition=" + condition + ", statements=" + statements + "]";
    }
    
}
