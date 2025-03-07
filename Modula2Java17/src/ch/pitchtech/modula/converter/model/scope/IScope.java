package ch.pitchtech.modula.converter.model.scope;

import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

/**
 * A scope for resolving identifier. A scope can be a module, a procedure, a WITH statement, etc.
 * <p>
 * By default, identifiers are resolved in the current local scope only. Use {@link #full()} to create a
 * corresponding {@link IScope} that resolves in the current scope AND in all the parent scopes
 * recursively.
 * <p>
 * {@link IHasScope#getLocalScope()} returns the local scope. {@link IHasScope#getScope()} returns the
 * full scope, and is equivalent to calling {@link #full()} on the result of {@link IHasScope#getLocalScope()}.
 */
public interface IScope {
    
    public IScope getParentScope();
    
    public DefinitionModule resolveModule(String name);
    
    public ConstantDefinition resolveConstant(String name);
    
    public TypeDefinition resolveType(String name);
    
    public VariableDefinition resolveVariable(String name);
    
    public ProcedureDefinition resolveProcedure(String name);
    
    /**
     * Resolve the given identifier against any of the given natures (constant, type, etc).
     * <p>
     * When using on a {@link #full()} scope, this tries all natures on the current local scope, and then
     * recursively all natures on each of the parent scopes.
     */
    public default IDefinition resolve(String name, boolean constant, boolean type, boolean variable, boolean procedure) {
        if (constant) {
            ConstantDefinition constantDefinition = resolveConstant(name);
            if (constantDefinition != null)
                return constantDefinition;
        }
        if (type) {
            TypeDefinition typeDefinition = resolveType(name);
            if (typeDefinition != null)
                return typeDefinition;
        }
        if (variable) {
            VariableDefinition variableDefinition = resolveVariable(name);
            if (variableDefinition != null)
                return variableDefinition;
        }
        if (procedure) {
            ProcedureDefinition procedureDefinition = resolveProcedure(name);
            if (procedureDefinition != null)
                return procedureDefinition;
        }
        return null;
    }
    
    public default IScope full() {
        if (this instanceof ScopeResolver)
            return this;
        return new ScopeResolver(this);
    }
    
    public default ICompilationUnit getCompilationUnit() {
        IScope scope = this;
        while (scope != null) {
            if (scope instanceof CompilationUnitScope cus)
                return cus.getCompilationUnit();
            scope = scope.getParentScope();
        }
        return null;
    }
    
}
