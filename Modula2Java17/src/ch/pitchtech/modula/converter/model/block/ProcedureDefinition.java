package ch.pitchtech.modula.converter.model.block;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

public class ProcedureDefinition extends SourceElement implements INode, IDefinition {
    
    private final IHasScope parent;
    private final String name;
    private final List<FormalArgument> arguments = new ArrayList<>();
    private final LiteralType returnType; // null if void
    private final boolean builtIn;
    Set<ProcedureType> usedAsExprTypes = new LinkedHashSet<>();
    
    
    public ProcedureDefinition(SourceLocation sLoc, IHasScope parent, String name, LiteralType returnType, boolean builtIn) {
        super(sLoc);
        this.parent = parent;
        this.name = name;
        this.returnType = returnType;
        this.builtIn = builtIn;
    }
    
    @Override
    public IHasScope getParent() {
        return parent;
    }

    @Override
    public INode getParentNode() {
        return parent;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public List<FormalArgument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    public void addArguments(List<FormalArgument> arguments) {
        for (FormalArgument argument : arguments) {
            attach(argument, NodeAttachType.DEFAULT);
            this.arguments.add(argument);
        }
    }
    
    public BitSet getVarArgs() {
        BitSet result = new BitSet(arguments.size());
        for (int i = 0; i < arguments.size(); i++) {
            if (arguments.get(i).isVar())
                result.set(i);
        }
        return result;
    }
    
    public LiteralType getReturnType() {
        return returnType;
    }
    
    public boolean isBuiltIn() {
        return builtIn;
    }
    
    public Set<ProcedureType> getUsedAsExprTypes() {
        return usedAsExprTypes;
    }
    
    @Override
    public String toString() {
        return "ProcedureDefinition [name=" + name + ", arguments=" + arguments + ", returnType=" + returnType + "]";
    }

}
