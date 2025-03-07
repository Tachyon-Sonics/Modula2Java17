package ch.pitchtech.modula.converter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInScope;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class Application implements IHasScope {
    
    private final BuiltInScope builtIn;
    private final List<DefinitionModule> definitionModules = new ArrayList<>();
    private final List<ImplementationModule> implementationModules = new ArrayList<>();
    private final List<Module> modules = new ArrayList<>();
    private final CompilerOptions compilerOptions;
    
    
    public Application(CompilerOptions compilerOptions) {
        this.compilerOptions = compilerOptions;
        SourceLocation dummy = new SourceLocation(0, 0, 0, 0);
        DefinitionModule system = new DefinitionModule(dummy, "SYSTEM", this); // TODO SYSTEM
        definitionModules.add(system);
        builtIn = new BuiltInScope();
    }
    
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public BuiltInScope getBuiltIn() {
        return builtIn;
    }
    
    public List<DefinitionModule> getDefinitionModules() {
        return Collections.unmodifiableList(definitionModules);
    }
    
    public void addDefinitionModule(DefinitionModule definitionModule) {
        this.definitionModules.add(definitionModule);
    }
    
    public List<ImplementationModule> getImplementationModules() {
        return Collections.unmodifiableList(implementationModules);
    }
    
    public void addImplementationModule(ImplementationModule implementationModule) {
        this.implementationModules.add(implementationModule);
    }
    
    public List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }
    
    public void addModule(Module module) {
        this.modules.add(module);
    }
    
    @Override
    public INode getParentNode() {
        return null;
    }

    @Override
    public void setParentNode(INode parent) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<INode> getChildren() {
        List<INode> result = new ArrayList<>();
        result.addAll(definitionModules);
        result.addAll(implementationModules);
        result.addAll(modules);
        return result;
    }

    @Override
    public NodeAttachType getAttachType(INode child) {
        return NodeAttachType.DEFAULT;
    }
    
    // Scope

    private final class ScopeImplementation implements IScope {

        @Override
        public IScope getParentScope() {
            return null;
        }

        @Override
        public DefinitionModule resolveModule(String name) {
            return definitionModules.stream().filter((dm) -> dm.getName().equals(name)).findFirst().orElse(
                    builtIn.resolveModule(name));
        }

        @Override
        public ConstantDefinition resolveConstant(String name) {
            return null; // No application-wide or built-in constants
        }

        @Override
        public TypeDefinition resolveType(String name) {
            return builtIn.resolveType(name);
        }

        @Override
        public VariableDefinition resolveVariable(String name) {
            return null; // No application-wide or built-in variable
        }

        @Override
        public ProcedureDefinition resolveProcedure(String name) {
            return builtIn.resolveProcedure(name);
        }
    }
    
    private final IScope myScope = new ScopeImplementation();

    @Override
    public IScope getLocalScope() {
        return myScope;
    }

    @Override
    public String getJavaQualifierForItems() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "Application [definitionModules=" + definitionModules + ", implementationModules=" + implementationModules + ", modules=" + modules + "]";
    }

}
