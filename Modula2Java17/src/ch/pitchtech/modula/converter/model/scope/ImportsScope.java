package ch.pitchtech.modula.converter.model.scope;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;


public class ImportsScope implements IScope {
    
    private final Application application;
    private final ICompilationUnit compilationUnit;
    private final List<Import> imports;
    private final IScope parentScope;

    
    public ImportsScope(Application application, ICompilationUnit compilationUnit, List<Import> imports, IScope parentScope) {
        this.application = application;
        this.compilationUnit = compilationUnit;
        this.imports = imports;
        this.parentScope = parentScope;
    }

    @Override
    public IScope getParentScope() {
        return this.parentScope;
    }
    
    @Override
    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    @Override
    public DefinitionModule resolveModule(String name) {
        for (Import item : imports) {
            if (item.getFromModule().equals(name))
                return application.getScope().resolveModule(name);
        }
        return null;
    }
    
    /**
     * Importing an enum type also imports all enum members. This method takes all items imported from a module, and add enum
     * members of any imported enum type
     */
    private Collection<String> getExImportedItems(Import im) {
        if (im.isQualified())
            return Collections.emptyList();
        DefinitionModule fromModule = application.getScope().resolveModule(im.getFromModule());
        Set<String> result = new HashSet<>();
        for (String itemName : im.getItems()) {
            result.add(itemName);
            TypeDefinition typeDefinition = fromModule.getScope().resolveType(itemName);
            if (typeDefinition != null) {
                if (typeDefinition.getType() instanceof EnumerationType enumerationType) {
                    result.addAll(enumerationType.getElements());
                }
            }
        }
        return result;
    }
    
    private <E> E lookupOnImports(String name, BiFunction<IScope, String, E> lookup) {
        for (Import item : imports) {
            if (!item.isQualified()) {
                DefinitionModule definition = application.getLocalScope().resolveModule(item.getFromModule());
                if (definition == null)
                    throw new CompilationException(item, "Cannot resolve module: " + item.getFromModule());
                E result = lookup.apply(definition.getExportScope(), name);
                if (result != null) {
                    if (getExImportedItems(item).contains(name))
                        return result;
                }
            }
        }
        return null;
    }

    @Override
    public ConstantDefinition resolveConstant(String name) {
        return lookupOnImports(name, IScope::resolveConstant);
    }

    @Override
    public TypeDefinition resolveType(String name) {
        return lookupOnImports(name, IScope::resolveType);
    }

    @Override
    public VariableDefinition resolveVariable(String name) {
        return lookupOnImports(name, IScope::resolveVariable);
    }

    @Override
    public ProcedureDefinition resolveProcedure(String name) {
        return lookupOnImports(name, IScope::resolveProcedure);
    }

    @Override
    public String toString() {
        return "ImportsScope [compilationUnit=" + compilationUnit + ", imports=" + imports + "]";
    }

}
