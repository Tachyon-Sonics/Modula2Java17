package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class RepeatLoop extends SourceElement implements IStatement, IStatementsContainer {

    private final IExpression condition;
    private final List<IStatement> statements = new ArrayList<>();
    
    
    public RepeatLoop(SourceLocation sLoc, IExpression condition) {
        super(sLoc);
        this.condition = condition;
        attach(condition, NodeAttachType.READ_ACCESS);
    }
    
    public IExpression getCondition() {
        return condition;
    }
    
    public List<IStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }
    
    public void addStatements(List<IStatement> statements) {
        this.statements.addAll(statements);
        attach(statements, NodeAttachType.DEFAULT);
    }

    @Override
    public String toString() {
        return "RepeatLoop [condition=" + condition + ", statements=" + statements + "]";
    }

}
