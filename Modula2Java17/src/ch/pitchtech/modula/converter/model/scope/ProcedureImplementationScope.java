package ch.pitchtech.modula.converter.model.scope;

import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class ProcedureImplementationScope implements IScope {
    
    private final ProcedureImplementation procedureImplementation;
    

    public ProcedureImplementationScope(ProcedureImplementation procedureImplementation) {
        this.procedureImplementation = procedureImplementation;
    }

    @Override
    public IScope getParentScope() {
        return procedureImplementation.getParent().getLocalScope();
    }

    @Override
    public DefinitionModule resolveModule(String name) {
        return getParentScope().resolveModule(name);
    }

    @Override
    public ConstantDefinition resolveConstant(String name) {
        return procedureImplementation.getConstantDefinitions().stream().filter((cd) -> cd.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public TypeDefinition resolveType(String name) {
        return procedureImplementation.getTypeDefinitions().stream().filter((td) -> td.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public VariableDefinition resolveVariable(String name) {
        for (FormalArgument argument : procedureImplementation.getArguments()) {
            if (argument.getName().equals(name))
                return argument.asVariableDefinition(procedureImplementation);
        }
        return procedureImplementation.getVariableDefinitions().stream().filter((vd) -> vd.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public ProcedureDefinition resolveProcedure(String name) {
        return procedureImplementation.procedureDefinitions().filter((pd) -> pd.getName().equals(name)).findFirst().orElse(null);
    }
    
    public ProcedureImplementation getProcedureImplementation() {
        return procedureImplementation;
    }
}