package ch.pitchtech.modula.converter.model;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.CompilationUnitScope;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class ImplementationModule extends SourceElement implements ICompilationUnit, IImplemented, IHasScope, INode {

    private final DefinitionModule definition;
    private final Application application;
    private final List<Import> imports = new ArrayList<>();
    private List<ConstantDefinition> constantDefinitions = new ArrayList<>();
    private List<TypeDefinition> typeDefinitions = new ArrayList<>();
    private List<VariableDefinition> variableDefinitions = new ArrayList<>();
    private List<ProcedureImplementation> procedures = new ArrayList<>();
    private List<IStatement> beginStatements = new ArrayList<>();
    private List<IStatement> closeStatements = new ArrayList<>();

    
    
    public ImplementationModule(SourceLocation sLoc, DefinitionModule definition, Application application) {
        super(sLoc);
        this.definition = definition;
        this.application = application;
    }
    
    @Override
    public String getName() {
        return definition.getName();
    }

    public DefinitionModule getDefinition() {
        return definition;
    }
    
    @Override
    public Application getApplication() {
        return application;
    }

    @Override
    public List<Import> getImports() {
        return imports;
    }

    @Override
    public List<ConstantDefinition> getConstantDefinitions() {
        return constantDefinitions;
    }
    
    public void setConstantDefinitions(List<ConstantDefinition> constantDefinitions) {
        this.constantDefinitions = constantDefinitions;
    }

    @Override
    public List<TypeDefinition> getTypeDefinitions() {
        return typeDefinitions;
    }
    
    public void setTypeDefinitions(List<TypeDefinition> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }
    
    @Override
    public List<VariableDefinition> getVariableDefinitions() {
        return variableDefinitions;
    }

    public void setVariableDefinitions(List<VariableDefinition> variableDefinitions) {
        this.variableDefinitions = variableDefinitions;
    }
    
    @Override
    public List<ProcedureImplementation> getProcedures() {
        return procedures;
    }
    
    public void addProcedure(ProcedureImplementation procedure) {
        this.procedures.add(procedure);
        attach(procedures, NodeAttachType.DEFAULT);
    }
    
    public void addProcedureBefore(ProcedureImplementation before, ProcedureImplementation toAdd) {
        int index = procedures.indexOf(before);
        procedures.add(index, toAdd);
        attach(toAdd, NodeAttachType.DEFAULT);
    }
    
    @Override
    public List<IStatement> getBeginStatements() {
        return beginStatements;
    }
    
    public void setBeginStatements(List<IStatement> beginStatements) {
        this.beginStatements = beginStatements;
    }
    
    @Override
    public List<IStatement> getCloseStatements() {
        return closeStatements;
    }
    
    public void setCloseStatements(List<IStatement> closeStatements) {
        this.closeStatements = closeStatements;
    }
 
    // Node
    
    @Override
    public INode getParentNode() {
        return null;
    }

    @Override
    public List<INode> getChildren() {
        List<INode> result = new ArrayList<>();
        result.add(new NodeList<>(this, imports, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, constantDefinitions, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, typeDefinitions, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, variableDefinitions, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, procedures, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, beginStatements, NodeAttachType.DEFAULT));
        result.add(new NodeList<>(this, closeStatements, NodeAttachType.DEFAULT));
        return result;
    }

    // Scope
    
    private IScope myScope;

    @Override
    public IScope getLocalScope() {
        if (myScope == null)
            myScope = new CompilationUnitScope(this, application, imports, constantDefinitions, typeDefinitions,
                    variableDefinitions, procedures);
        return myScope;
    }

    @Override
    public String getJavaQualifierForItems() {
        return "private";
    }

    @Override
    public String toString() {
        return "ImplementationModule [definition=" + definition + "]";
    }

}
