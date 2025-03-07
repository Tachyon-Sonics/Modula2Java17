package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class WithStatement extends SourceElement implements IStatement, IStatementsContainer, IHasScope {
    
    private final IHasScope parent;
    private final IExpression expression;
    private final List<IStatement> statements = new ArrayList<>();
    private String localVariableName;
    
    
    public WithStatement(SourceLocation sLoc, IHasScope parent, IExpression expression) {
        super(sLoc);
        this.parent = parent;
        this.expression = expression;
        attach(expression, NodeAttachType.READ_ACCESS);
    }

    public IExpression getExpression() {
        return expression;
    }
    
    public List<IStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }
    
    public void addStatements(List<IStatement> statements) {
        this.statements.addAll(statements);
        attach(statements, NodeAttachType.DEFAULT);
    }
    
    public String getLocalVariableName() {
        return localVariableName;
    }
    
    public void setLocalVariableName(String localVariableName) {
        this.localVariableName = localVariableName;
    }

    // IHasScope

    class WithStatementScope implements IScope {

        @Override
        public IScope getParentScope() {
            return parent.getLocalScope();
        }

        @Override
        public DefinitionModule resolveModule(String name) {
            return null;
        }

        @Override
        public ConstantDefinition resolveConstant(String name) {
            return null;
        }

        @Override
        public TypeDefinition resolveType(String name) {
            return null;
        }

        @Override
        public VariableDefinition resolveVariable(String name) {
            RecordType recordType = (RecordType) TypeResolver.resolveType(getParentScope(), expression.getType(getParentScope()));
            for (VariableDefinition item : recordType.getExElements()) {
                if (item.getName().equals(name)) {
                    // Create a copy within the scope of the WITH statement
                    return new VariableDefinition(
                            item.getSourceLocation(), 
                            WithStatement.this, 
                            item.getName(), 
                            item.getType());
                }
            }
            return null;
        }

        @Override
        public ProcedureDefinition resolveProcedure(String name) {
            return null;
        }

    }
    
    private WithStatementScope myScope;

    @Override
    public IScope getLocalScope() {
        if (myScope == null)
            myScope = new WithStatementScope();
        return myScope;
    }

    @Override
    public String getJavaQualifierForItems() {
        throw new CompilerException(this, "Cannot put declarations in a WITH statement");
    }

    @Override
    public String toString() {
        return "WithStatement [expression=" + expression + ", statements=" + statements + "]";
    }

}
