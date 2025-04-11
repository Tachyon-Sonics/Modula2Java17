package ch.pitchtech.modula.converter.model.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.ProcedureImplementationScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class ProcedureImplementation extends SourceElement implements IHasScope, INode {
    
    private final IHasScope parent;
    private final String name;
    private final List<FormalArgument> arguments = new ArrayList<>();
    private final LiteralType returnType;
    
    private final List<ConstantDefinition> constantDefinitions = new ArrayList<>();
    private final List<TypeDefinition> typeDefinitions = new ArrayList<>();
    private final List<VariableDefinition> variableDefinitions = new ArrayList<>();
    private final List<ProcedureImplementation> procedures = new ArrayList<>();
    
    private final List<IStatement> statements = new ArrayList<>();
    
    private String prefix;
    private Set<String> variableNames;
    private Set<ILocalData> readData = new HashSet<>();
    private Set<ILocalData> writeData = new HashSet<>();
    private Set<ILocalData> addressedData = new HashSet<>();
    
    private final Set<ProcedureType> usedAsExprTypes = new LinkedHashSet<>();

    
    public ProcedureImplementation(SourceLocation sLoc, IHasScope parent, String name, LiteralType returnType) {
        super(sLoc);
        this.parent = parent;
        this.name = name;
        this.returnType = returnType;
    }

    /**
     * Name of this procedure, as it appeared in Modula-2
     */
    public String getName() {
        return name;
    }

    public List<FormalArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    public void addArguments(List<FormalArgument> arguments) {
        for (FormalArgument argument : arguments) {
            addArgument(argument);
        }
    }

    public void addArgument(FormalArgument argument) {
        attach(argument, NodeAttachType.DEFAULT);
        this.arguments.add(argument);
    }
    
    public FormalArgument getArgument(String name) {
        for (FormalArgument arg : arguments) {
            if (arg.getName().equals(name))
                return arg;
        }
        return null;
    }
    
    public LiteralType getReturnType() {
        return returnType;
    }
    
    public ProcedureDefinition findDefinition() {
        if (parent instanceof ImplementationModule implementationModule) {
            DefinitionModule definitionModule = implementationModule.getDefinition();
            if (definitionModule != null) {
                return definitionModule.getProcedureDefinitions().stream()
                        .filter((pd) -> pd.getName().equals(getFullName()))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }
    
    public List<ConstantDefinition> getConstantDefinitions() {
        return Collections.unmodifiableList(constantDefinitions);
    }
    
    public void addConstantDefinitions(List<ConstantDefinition> definitions) {
        this.constantDefinitions.addAll(definitions);
        attach(definitions, NodeAttachType.DEFAULT);
    }
    
    public List<TypeDefinition> getTypeDefinitions() {
        return Collections.unmodifiableList(typeDefinitions);
    }
    
    public void removeTypeDefinition(TypeDefinition typeDefinition) {
        if (!typeDefinitions.contains(typeDefinition)) {
            throw new CompilerException(typeDefinition, "Type definition {0} does not belong to {1}", typeDefinition, this);
        }
        typeDefinitions.remove(typeDefinition);
    }
    
    public void addTypeDefinitions(List<TypeDefinition> definitions) {
        this.typeDefinitions.addAll(definitions);
        attach(definitions, NodeAttachType.DEFAULT);
    }
    
    public List<VariableDefinition> getVariableDefinitions() {
        return Collections.unmodifiableList(variableDefinitions);
    }
    
    public void addVariableDefinitions(List<VariableDefinition> definitions) {
        this.variableDefinitions.addAll(definitions);
        attach(definitions, NodeAttachType.DEFAULT);
    }
    
    public List<ProcedureImplementation> getProcedures() {
        return Collections.unmodifiableList(procedures);
    }
    
    public ProcedureImplementation getProcedure(String name) {
        return procedures.stream().filter((p) -> p.getName().equals(name)).findFirst().orElse(null);
    }
    
    public void addProcedure(ProcedureImplementation procedure) {
        this.procedures.add(procedure);
        attach(procedure, NodeAttachType.DEFAULT);
    }
    
    public void addProcedureBefore(ProcedureImplementation before, ProcedureImplementation toAdd) {
        int index = procedures.indexOf(before);
        procedures.add(index, toAdd);
        attach(toAdd, NodeAttachType.DEFAULT);
    }
    
    public Stream<ProcedureDefinition> procedureDefinitions() {
        return procedures.stream().map(ProcedureImplementation::asDefinition);
    }
    
    public List<IStatement> getStatements() {
        return Collections.unmodifiableList(statements);
    }
    
    public void addStatements(List<IStatement> statements) {
        this.statements.addAll(statements);
        attach(statements, NodeAttachType.DEFAULT);
    }
    
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void insertPrefix(String prefix) {
        if (this.prefix == null)
            this.prefix = prefix;
        else
            this.prefix = prefix + this.prefix;
    }
    
    /**
     * If this procedure is nested in another procedure, the names of original arguments and variables
     */
    public Set<String> getVariableNames() {
        return variableNames;
    }

    public void setVariableNames(Set<String> variableNames) {
        this.variableNames = variableNames;
    }

    /**
     * Full name of this procedure. Same as {@link #getName()}, with possibly prefixes appended
     * if this was a nested procedure (a procedure inside another one).
     */
    public String getFullName() {
        if (prefix != null)
            return prefix + name;
        return name;
    }
    
    public Set<ILocalData> getReadData() {
        return readData;
    }

    public Set<ILocalData> getWriteData() {
        return writeData;
    }
    
    public Set<ILocalData> getAddressedData() {
        return addressedData;
    }
    
    public void setAddressedData(Set<ILocalData> addressedData) {
        this.addressedData = addressedData;
    }

    @SuppressWarnings("unchecked")
    public <E extends ILocalData> Collection<E> getRead(Class<E> what) {
        return (Collection<E>) readData.stream().filter(what::isInstance).toList();
    }

    @SuppressWarnings("unchecked")
    public <E extends ILocalData> Collection<E> getWrite(Class<E> what) {
        return (Collection<E>) writeData.stream().filter(what::isInstance).toList();
    }
    
    @SuppressWarnings("unchecked")
    public <E extends ILocalData> Collection<E> getAddressed(Class<E> what) {
        return (Collection<E>) addressedData.stream().filter(what::isInstance).toList();
    }
    
    public <E extends ILocalData> Collection<E> getAccessed(Class<E> what) {
        Set<E> result = new HashSet<>();
        result.addAll(getRead(what));
        result.addAll(getWrite(what));
        result.addAll(getAddressed(what));
        return result;
    }
    
    public Set<String> getAccessedNames() {
        Set<String> result = new HashSet<>();
        result.addAll(readData.stream().map(ILocalData::getName).toList());
        result.addAll(writeData.stream().map(ILocalData::getName).toList());
        result.addAll(addressedData.stream().map(ILocalData::getName).toList());
        return result;
    }
    
    public Set<String> getWrittenOrAddressedNames() {
        Set<String> result = new HashSet<>();
        result.addAll(writeData.stream().map(ILocalData::getName).toList());
        result.addAll(addressedData.stream().map(ILocalData::getName).toList());
        return result;
    }
    
    public ProcedureDefinition asDefinition() {
        ProcedureDefinition result = new ProcedureDefinition(super.getSourceLocation(), parent, name, returnType, false);
        result.addArguments(this.arguments);
        result.usedAsExprTypes = this.usedAsExprTypes;
        return result;
    }
    
    private ProcedureImplementationScope myScope = null;

    @Override
    public IScope getLocalScope() {
        if (myScope == null)
            myScope = new ProcedureImplementationScope(this);
        return myScope;
    }

    @Override
    public String getJavaQualifierForItems() {
        return "";
    }
    
    public IHasScope getParent() {
        return parent;
    }

    @Override
    public INode getParentNode() { // TODO X try to get rid of the override, then remove getParentInTree
        return parent;
    }
    
    public INode getParentInTree() {
        return super.getParentNode();
    }

    @Override
    public String toString() {
        return "ProcedureImplementation [name=" + name + ", arguments=" + arguments + ", returnType=" + returnType + "]";
    }
    
}
