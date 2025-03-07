package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class LoopLoop extends SourceElement implements IStatement, IStatementsContainer {
    
    private final List<IStatement> statements = new ArrayList<>();


    public LoopLoop(SourceLocation sourceLocation) {
        super(sourceLocation);
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
        return "LoopLoop [statements=" + statements + "]";
    }

}
