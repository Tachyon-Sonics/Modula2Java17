package ch.pitchtech.modula.converter.model.builtin;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public enum BuiltInProcedure {
    EXIT(null, false),
    INC(null, true),
    DEC(null, true),
    INCL(null, true),
    EXCL(null, true),
    HALT(null, false),
    NEW(null, true),
    DISPOSE(null, true),
    MIN(BuiltInType.javaInt(), false) {
        
        @Override
        public IType getReturnTypeFor(IType forType) {
            if (forType instanceof EnumerationType)
                return forType;
            return super.getReturnTypeFor(forType);
        }
        
    },
    MAX(BuiltInType.javaInt(), false) {
        
        @Override
        public IType getReturnTypeFor(IType forType) {
            if (forType instanceof EnumerationType)
                return forType;
            return super.getReturnTypeFor(forType);
        }
        
    },
    NOT(() -> BuiltInType.BOOLEAN, false),
    ABS(BuiltInType.javaInt(), false) {

        @Override
        public IType getReturnTypeFor(IType forType) {
            if (forType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                if (biType.equals(BuiltInType.LONGCARD))
                    return new LiteralType(BuiltInType.LONGCARD); // long Math.abs(long)
                if (BuiltInType.getNumericTypes(false).contains(biType))
                    return new LiteralType(BuiltInType.LONGINT); // int Math.abs(int)
                return new LiteralType(biType); // float, double -> same
            }
            return super.getReturnTypeFor(forType);
        }
        
    },
    SIZE(BuiltInType.javaInt(), false),
    TSIZE(BuiltInType.javaInt(), false),
    ORD(BuiltInType.javaInt(), false),
    HIGH(BuiltInType.javaInt(), false),
    SHIFT(BuiltInType.javaInt(), false),
    ODD(() -> BuiltInType.BOOLEAN, false),
    CHR(() -> BuiltInType.CHAR, false),
    ADR(() -> BuiltInType.ADDRESS, false),
    VAL(() -> BuiltInType.ADDRESS, false);
    
    
    private final Supplier<BuiltInType> returnType;
    private final boolean writeFirstArgument;
    
    
    private BuiltInProcedure(Supplier<BuiltInType> returnType, boolean writeFirstArgument) {
        this.returnType = returnType;
        this.writeFirstArgument = writeFirstArgument;
    }
    
    public BuiltInType getReturnType() {
        if (returnType == null)
            return null;
        return returnType.get();
    }
    
    public boolean isWriteFirstArgument() {
        return writeFirstArgument;
    }

    public IType getReturnTypeFor(IType forType) {
        return new LiteralType(returnType.get());
    }
    
    public static Set<String> getBuiltInFunctionNames() {
        Set<String> result = new HashSet<>();
        for (BuiltInProcedure bp : BuiltInProcedure.values()) {
            if (bp.getReturnType() != null)
                result.add(bp.name());
        }
        return result;
    }
    
}
