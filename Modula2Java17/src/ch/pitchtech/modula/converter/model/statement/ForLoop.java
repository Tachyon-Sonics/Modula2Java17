package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

// // FOR ident ':=' expression TO expression (BY constExpression)? DO statementSequence END
public class ForLoop extends SourceElement implements IStatement, IStatementsContainer {
    
    private final Identifier identifier;
    private final IExpression fromExpression;
    private final IExpression toExpression;
    private final IExpression byExpression; // Optional
    private final List<IStatement> statements = new ArrayList<>();
    
    
    public ForLoop(SourceLocation sourceLocation, Identifier identifier, 
            IExpression fromExpression, IExpression toExpression, IExpression byExpression) {
        super(sourceLocation);
        this.identifier = identifier;
        this.fromExpression = fromExpression;
        this.toExpression = toExpression;
        this.byExpression = byExpression;
        attach(identifier, NodeAttachType.WRITE_ACCESS);
        attach(fromExpression, NodeAttachType.READ_ACCESS);
        attach(toExpression, NodeAttachType.READ_ACCESS);
        if (byExpression != null)
            attach(byExpression, NodeAttachType.READ_ACCESS);
    }
    
    public Identifier getIdentifier() {
        return identifier;
    }

    public IExpression getFromExpression() {
        return fromExpression;
    }

    public IExpression getToExpression() {
        return toExpression;
    }
    
    public IExpression getByExpression() {
        return byExpression;
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
        return "ForLoop [identifier=" + identifier + ", fromExpression=" + fromExpression + ", toExpression=" + toExpression + ", byExpression=" + byExpression
                + ", statements=" + statements + "]";
    }

}
