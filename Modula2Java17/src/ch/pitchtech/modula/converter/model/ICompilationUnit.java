package ch.pitchtech.modula.converter.model;

import java.util.List;

import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

/**
 * Common interface for {@link DefinitionModule}, {@link ImplementationModule} and {@link Module}.
 */
public interface ICompilationUnit extends INode, IHasScope {
    
    public String getName();
    
    public List<Import> getImports();
    
    public List<ConstantDefinition> getConstantDefinitions();
    
    public List<TypeDefinition> getTypeDefinitions();
    
    public List<VariableDefinition> getVariableDefinitions();
    
    
    /**
     * If this is a definition module, returns this; if this is an implementation module,
     * returns the corresponding definition module; if this is a module, returns null
     */
    public default DefinitionModule getDefinitionModule() {
        if (this instanceof DefinitionModule def)
            return def;
        else if (this instanceof ImplementationModule impl)
            return impl.getDefinition();
        return null;
    }
    
    public Application getApplication();
    
    /**
     * Get from which compilation module the given type identifier is imported.
     * Returns this, if the identifier is declared in this compilation unit.
     * Else, return the definition module from which it is inported.
     */
    public default ICompilationUnit getCompilationUnitDeclaringType(String identifier) {
        for (TypeDefinition td : getTypeDefinitions()) {
            if (td.getName().equals(identifier))
                return this;
        }
        for (Import item : getImports()) {
            if (item.getItems() != null && item.getItems().contains(identifier)) {
                String moduleName = item.getFromModule();
                return getScope().resolveModule(moduleName);
            }
        }
        if (this instanceof ImplementationModule implementationModule)
            return implementationModule.getDefinitionModule().getCompilationUnitDeclaringType(identifier);
        return null;
    }
    
    public default TypeDefinition getEnumTypeForEnumMember(String name) {
        for (TypeDefinition td : getTypeDefinitions()) {
            if (td.getType() instanceof EnumerationType enumerationType) {
                if (enumerationType.getElements().contains(name))
                    return td;
            }
        }
        return null;
    }

}
