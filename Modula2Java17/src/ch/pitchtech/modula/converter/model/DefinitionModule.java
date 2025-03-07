package ch.pitchtech.modula.converter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.CompilationUnitScope;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class DefinitionModule extends SourceElement implements ICompilationUnit, IHasScope, INode {

    private final String name;
    private final Application application;
    private final List<Import> imports = new ArrayList<>();
    private List<ConstantDefinition> constantDefinitions = new ArrayList<>();
    private List<TypeDefinition> typeDefinitions = new ArrayList<>();
    private List<VariableDefinition> variableDefinitions = new ArrayList<>();
    private List<ProcedureDefinition> procedureDefinitions = new ArrayList<>();

    
    public DefinitionModule(SourceLocation sLoc, String name, Application application) {
        super(sLoc);
        this.name = name;
        this.application = application;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Application getApplication() {
        return application;
    }
    
    public ImplementationModule getImplementation() {
        for (ImplementationModule implementation : application.getImplementationModules()) {
            if (implementation.getDefinition() == this)
                return implementation;
        }
        return null;
    }
    
    public boolean isImplemented() {
        return getImplementation() != null;
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

    public List<ProcedureDefinition> getProcedureDefinitions() {
        return procedureDefinitions;
    }

    public void setProcedureDefinitions(List<ProcedureDefinition> procedureDefinitions) {
        this.procedureDefinitions = procedureDefinitions;
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
        result.add(new NodeList<>(this, procedureDefinitions, NodeAttachType.DEFAULT));
        return result;
    }
    
    // Scope
    
    private IScope myScope;
    private IScope definitionScope;

    @Override
    public IScope getLocalScope() {
        if (myScope == null)
            myScope = new CompilationUnitScope(this, application, imports, constantDefinitions, typeDefinitions,
                    variableDefinitions, procedureDefinitions);
        return myScope;
    }
    
    /**
     * Scope limited to what is defined by this definition module.
     * Do not recurse into imports
     */
    public IScope getExportScope() {
        if (definitionScope == null)
            definitionScope = new CompilationUnitScope(this, application, Collections.emptyList(), constantDefinitions, typeDefinitions,
                    variableDefinitions, procedureDefinitions);
        return definitionScope;
    }

    @Override
    public String getJavaQualifierForItems() {
        return "public";
    }

    @Override
    public String toString() {
        return "DefinitionModule [name=" + name + "]";
    }

}
