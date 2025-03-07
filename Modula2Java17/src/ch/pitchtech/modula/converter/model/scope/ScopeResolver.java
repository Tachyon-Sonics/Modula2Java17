package ch.pitchtech.modula.converter.model.scope;

import java.util.function.BiFunction;

import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;


/**
 * Given a scope (as a {@link IScope}), implements the resolution of identifier by looking in
 * the given scope, and recursively in the parent scopes
 */
public class ScopeResolver implements IScope {
    
    private final IScope target;
    
    
    public ScopeResolver(IScope target) {
        if (target instanceof ScopeResolver)
            throw new IllegalArgumentException();
        this.target = target;
    }
    
    public IScope getTarget() {
        return target;
    }

    @Override
    public IScope getParentScope() {
        // We are already a "full" scope. No parent:
        return null;
    }

    private <E> E resolveItem(String name, BiFunction<IScope, String, E> lookup) {
        IScope curScope = target;
        while (curScope != null) {
            if (curScope instanceof ScopeResolver)
                throw new IllegalStateException(); // Each level must be a local scope
            E result = lookup.apply(curScope, name);
            if (result != null)
                return result;
            curScope = curScope.getParentScope();
        }
        return null;
    }

    @Override
    public DefinitionModule resolveModule(String name) {
        return resolveItem(name, IScope::resolveModule);
    }
    
    @Override
    public ConstantDefinition resolveConstant(String name) {
        return resolveItem(name, IScope::resolveConstant);
    }

    @Override
    public TypeDefinition resolveType(String name) {
        return resolveItem(name, IScope::resolveType);
    }

    @Override
    public VariableDefinition resolveVariable(String name) {
        return resolveItem(name, IScope::resolveVariable);
    }

    @Override
    public ProcedureDefinition resolveProcedure(String name) {
        return resolveItem(name, IScope::resolveProcedure);
    }

    @Override
    public IDefinition resolve(String name, boolean constant, boolean type, boolean variable, boolean procedure) {
        return resolveItem(name, (IScope scope, String name0) -> {
            return scope.resolve(name0, constant, type, variable, procedure);
        });
    }

}
