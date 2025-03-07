package ch.pitchtech.modula.converter.model.scope;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.EnumItemIdentifier;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class CompilationUnitScope implements IScope {

    private final ICompilationUnit compilationUnit;
    private final Application application;
    private final DefinitionModule definition;
    private final List<Import> imports;
    private final List<ConstantDefinition> constantDefinitions;
    private final List<TypeDefinition> typeDefinitions;
    private final List<VariableDefinition> variableDefinitions;
    private final List<ProcedureDefinition> procedureDefinitions;
    private final List<ProcedureImplementation> procedureImplementations;

    
    /**
     * Create the scope for a {@link DefinitionModule}
     */
    public CompilationUnitScope(DefinitionModule definitionModule, Application application, List<Import> imports,
            List<ConstantDefinition> constantDefinitions,
            List<TypeDefinition> typeDefinitions, List<VariableDefinition> variableDefinitions, 
            List<ProcedureDefinition> procedureDefinitions) {
        this(definitionModule, application, null, imports, constantDefinitions, typeDefinitions, variableDefinitions,
                procedureDefinitions, Collections.emptyList());
    }

    /**
     * Create the scope for a {@link ImplementationModule}
     */
    public CompilationUnitScope(ImplementationModule implementationModule, Application application, List<Import> imports,
            List<ConstantDefinition> constantDefinitions,
            List<TypeDefinition> typeDefinitions, List<VariableDefinition> variableDefinitions, 
            List<ProcedureImplementation> procedureImplementations) {
        this(implementationModule, application, implementationModule.getDefinition(), imports, constantDefinitions, typeDefinitions, variableDefinitions,
                Collections.emptyList(), procedureImplementations);
    }
    
    public CompilationUnitScope(ch.pitchtech.modula.converter.model.Module compilationUnit, Application application, DefinitionModule definition, List<Import> imports,
            List<ConstantDefinition> constantDefinitions,
            List<TypeDefinition> typeDefinitions, List<VariableDefinition> variableDefinitions, 
            List<ProcedureImplementation> procedureImplementations) {
        this(compilationUnit, application, null, imports, constantDefinitions, typeDefinitions, variableDefinitions,
                Collections.emptyList(), procedureImplementations);
    }
    
    private CompilationUnitScope(ICompilationUnit compilationUnit, Application application, DefinitionModule definitionModule, List<Import> imports,
            List<ConstantDefinition> constantDefinitions,
            List<TypeDefinition> typeDefinitions, List<VariableDefinition> variableDefinitions, 
            List<ProcedureDefinition> procedureDefinitions, List<ProcedureImplementation> procedureImplementations) {
        this.compilationUnit = compilationUnit;
        this.application = application;
        this.definition = definitionModule;
        this.imports = imports;
        this.constantDefinitions = constantDefinitions;
        this.typeDefinitions = typeDefinitions;
        this.variableDefinitions = variableDefinitions;
        this.procedureDefinitions = procedureDefinitions;
        this.procedureImplementations = procedureImplementations;
    }
    
    @Override
    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    @Override
    public IScope getParentScope() {
        if (compilationUnit instanceof ImplementationModule implementationModule) {
            // Look IMPORTS, then DEFINITION
            IScope grandParentScope = implementationModule.getDefinition().getLocalScope();
            return new ImportsScope(application, implementationModule, imports, grandParentScope);
        }
        // Look IMPORTS, then application
        IScope grandParentScope = application.getLocalScope();
        return new ImportsScope(application, compilationUnit, imports, grandParentScope);
    }

    @Override
    public DefinitionModule resolveModule(String name) {
        // Return null, because the parent scope (Imports) will handle it
        return null;
    }

    @Override
    public ConstantDefinition resolveConstant(String name) {
        for (ConstantDefinition cd : constantDefinitions) {
            if (cd.getName().equals(name))
                return cd;
        }
        /*
         * If an enumeration type is imported, all members of the enumeration type are implicitely imported
         * too, and show up as constants
         */
        for (TypeDefinition td : typeDefinitions) {
            if (td.getType() instanceof EnumerationType enumerationType) {
                if (enumerationType.getElements().contains(name)) {
                    return new ConstantDefinition(td.getSourceLocation(), compilationUnit, name, 
                            new EnumItemIdentifier(td.getSourceLocation(), compilationUnit, name, enumerationType));
                }
            }
        }
        return null;
    }

    @Override
    public TypeDefinition resolveType(String name) {
        return typeDefinitions.stream().filter((td) -> td.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public VariableDefinition resolveVariable(String name) {
        for (VariableDefinition vd : variableDefinitions) {
            if (vd.getName().equals(name))
                return vd;
        }
        return null;
    }

    private Stream<ProcedureDefinition> getProcedureImplementationsAsDefinitions() {
        return procedureImplementations.stream().map(ProcedureImplementation::asDefinition);
    }

    @Override
    public ProcedureDefinition resolveProcedure(String name) {
        if (definition != null) {
            ProcedureDefinition result = definition.getScope().resolveProcedure(name);
            if (result != null)
                return result;
        }
        ProcedureDefinition result = getProcedureImplementationsAsDefinitions()
                .filter((pd) -> pd.getName().equals(name)).findFirst().orElse(null);
        if (result != null)
            return result;
        if (!procedureDefinitions.isEmpty())
            result = procedureDefinitions.stream().filter((pd) -> pd.getName().equals(name)).findFirst().orElse(null);
        if (result != null)
            return result;
        return null;
    }

    @Override
    public String toString() {
        return "CompilationUnitScope [compilationUnit=" + compilationUnit + "]";
    }

}
