package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class CaseStatement extends SourceElement implements IStatement, IStatementsContainer {
    
    private final IExpression expression;
    private final List<CaseItem> cases = new ArrayList<>();
    private List<IStatement> elseStatements = null;
    
    
    public CaseStatement(SourceLocation sLoc, IExpression expression) {
        super(sLoc);
        this.expression = expression;
        attach(expression, NodeAttachType.READ_ACCESS);
    }

    public IExpression getExpression() {
        return expression;
    }
    
    public List<CaseItem> getCases() {
        return Collections.unmodifiableList(cases);
    }
    
    public void addCase(CaseItem caseItem) {
        cases.add(caseItem);
        attach(caseItem.getLabelExpressions(), NodeAttachType.READ_ACCESS);
        attach(caseItem.statements(), NodeAttachType.DEFAULT);
    }

    /**
     * @return <tt>null</tt> if this CASE has no "ELSE". Empty list if there is a "ELSE" but with no statement!
     */
    public List<IStatement> getElseStatements() {
        if (elseStatements == null)
            return null;
        return Collections.unmodifiableList(elseStatements);
    }
    
    public void setElseStatements(List<IStatement> elseStatements) {
        this.elseStatements = elseStatements;
        attach(elseStatements, NodeAttachType.DEFAULT);
    }

    @Override
    public String toString() {
        return "CaseStatement [expression=" + expression + ", cases=" + cases + "]";
    }

}
