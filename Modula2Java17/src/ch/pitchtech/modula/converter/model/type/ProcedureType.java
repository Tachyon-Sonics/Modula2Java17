package ch.pitchtech.modula.converter.model.type;

import java.util.BitSet;
import java.util.List;

import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class ProcedureType extends TypeBase implements INamedType {
    
    private String name;
    private final List<IType> argumentTypes;
    private final BitSet varArguments;
    private final IType returnType; // null if void
    
    
    public ProcedureType(SourceLocation sLoc, IHasScope scopeUnit, String name, List<IType> argumentTypes, BitSet varArguments, IType returnType) {
        super(sLoc, scopeUnit);
        this.name = name;
        this.argumentTypes = argumentTypes;
        this.varArguments = varArguments;
        this.returnType = returnType;
    }
    
    public ProcedureType(ProcedureDefinition procedureDefinition) {
        this(procedureDefinition.getSourceLocation(), 
                procedureDefinition.getParent(), 
                procedureDefinition.getName(),
                procedureDefinition.getArguments().stream().map(FormalArgument::getType).toList(),
                procedureDefinition.getVarArgs(),
                procedureDefinition.getReturnType());
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<IType> getArgumentTypes() {
        return argumentTypes;
    }
    
    public BitSet getVarArguments() {
        return varArguments;
    }
    
    public boolean isVarArg(int argIndex) {
        return varArguments.get(argIndex);
    }

    /**
     * return type, or <tt>null</tt> if void
     */
    public IType getReturnType() {
        return returnType;
    }
    
    public boolean isProc() {
        return getReturnType() == null && getArgumentTypes().isEmpty() && getName().equals(BuiltInType.PROC.name());
    }
    
    /**
     * Overriden to mark all PROC types as equal (they might be generated on the fly)
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof ProcedureType other) {
            if (other.isProc() && this.isProc())
                return true;
        }
        return super.equals(o);
    }
    
    /**
     * Overriden to mark all PROC types as equal (they might be generated on the fly)
     */
    @Override
    public int hashCode() {
        if (isProc())
            return 42;
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "ProcedureType [name=" + name + ", argumentTypes=" + argumentTypes + ", varArguments=" + varArguments + ", returnType=" + returnType + "]";
    }

}
