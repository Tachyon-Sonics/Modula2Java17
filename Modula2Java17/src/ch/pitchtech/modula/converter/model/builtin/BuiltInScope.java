package ch.pitchtech.modula.converter.model.builtin;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class BuiltInScope implements IScope {
    
    private List<ProcedureDefinition> procedures = new ArrayList<>();
    private List<TypeDefinition> types = new ArrayList<>();
    
    
    public BuiltInScope() {
        // TODO continue, separate built-in from system
        for (BuiltInType type : BuiltInType.values()) {
            SourceLocation dummy = new SourceLocation(0, 0, 0, 0);
            TypeDefinition typeDefinition = new TypeDefinition(dummy, null, type.name(), new LiteralType(type));
            types.add(typeDefinition);
        }
        for (BuiltInProcedure proc : BuiltInProcedure.values()) {
            SourceLocation dummy = new SourceLocation(0, 0, 0, 0);
            BuiltInType rType = proc.getReturnType();
            LiteralType returnType = (rType == null ? null : new LiteralType(rType));
            ProcedureDefinition procedure = new ProcedureDefinition(dummy, null, proc.name(), returnType, true);
            procedures.add(procedure); // TODO review argument and return types
        }
    }

    @Override
    public IScope getParentScope() {
        return null;
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
        return types.stream().filter((td) -> td.getName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public VariableDefinition resolveVariable(String name) {
        return null;
    }

    @Override
    public ProcedureDefinition resolveProcedure(String name) {
        return procedures.stream().filter((pd) -> pd.getName().equals(name)).findFirst().orElse(null);
    }

}
