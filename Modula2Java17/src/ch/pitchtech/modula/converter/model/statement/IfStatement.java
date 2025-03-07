package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class IfStatement extends SourceElement implements IStatement, IStatementsContainer {
    
    private final IExpression condition;
    private final List<IStatement> thenStatements = new ArrayList<>();
    private final List<ElsIfStatement> elseifs = new ArrayList<>();
    private final List<IStatement> elseStatements = new ArrayList<>();
    
    
    public IfStatement(SourceLocation sLoc, IExpression condition) {
        super(sLoc);
        this.condition = condition;
        attach(condition, NodeAttachType.READ_ACCESS);
    }

    public IExpression getCondition() {
        return condition;
    }
    
    public List<IStatement> getThenStatements() {
        return Collections.unmodifiableList(thenStatements);
    }
    
    public void addThenStatements(List<IStatement> statements) {
        this.thenStatements.addAll(statements);
        attach(statements, NodeAttachType.DEFAULT);
    }
    
    public List<ElsIfStatement> getElsifs() {
        return Collections.unmodifiableList(elseifs);
    }
    
    public void addElsif(ElsIfStatement elsif) {
        this.elseifs.add(elsif);
        attach(elsif, NodeAttachType.DEFAULT);
    }
    
    public List<IStatement> getElseStatements() {
        return Collections.unmodifiableList(elseStatements);
    }
    
    public void addElseStatements(List<IStatement> statements) {
        elseStatements.addAll(statements);
        attach(statements, NodeAttachType.DEFAULT);
    }

    @Override
    public String toString() {
        return "IfStatement [condition=" + condition + ", thenStatements=" + thenStatements + ", elseifs=" + elseifs + ", elseStatements=" + elseStatements
                + "]";
    }

}
