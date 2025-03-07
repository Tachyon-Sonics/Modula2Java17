package ch.pitchtech.modula.converter.model.builtin;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.model.scope.IScope;

public enum BuiltInType {
    INTEGER(2, "short", "Short", true, true, false),
    SHORTINT(1, "byte", "Byte", true, true, false),
    LONGINT(4, "int", "Integer", true, true, false),
    CARDINAL(4, "int", "Integer", true, false, false),
    SHORTCARD(2, "short", "Short", true, false, false),
    LONGCARD(8, "long", "Long", true, false, false),
    REAL(4, "float", "Float", true, true, true),
    LONGREAL(8, "double", "Double", true, true, true),
    CHAR(1, "char", "Character"),
    BOOLEAN(1, "boolean", "Boolean"),
    BITSET(2, "Runtime.RangSet", "Runtime.RangSet"),
    ADDRESS(8, "Object", "Object"),
    PROC(8, "Runnable", "Runnable"),
    BYTE(1, "byte", "Byte"),
    WORD(2, "short", "Short"),
    LONGWORD(4, "int", "Integer"),
    STRING(8, "String", "String"); // For constants only
    
    private final int size; // TODO differentiate java size and modula-2 size (with eg. LONGCARD = 4)
    private final String javaType;
    private final String boxedType;
    private final boolean numeric;
    private final boolean signed;
    private final boolean decimal;
    
    
    private BuiltInType(int size, String javaType, String boxedType) {
        this(size, javaType, boxedType, false, false, false);
    }
    
    private BuiltInType(int size, String javaType, String boxedType, boolean numeric, boolean signed, boolean decimal) {
        this.size = size;
        this.javaType = javaType;
        this.boxedType = boxedType;
        this.numeric = numeric;
        this.signed = signed;
        this.decimal = decimal;
    }
    
    public int getSize() {
        return size;
    }
    
    public String getJavaType() {
        return javaType;
    }
    
    public String getBoxedType() {
        return boxedType;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public boolean isSigned() {
        return signed;
    }
    
    /**
     * @return if the type is floating-point
     */
    public boolean isDecimal() {
        return decimal;
    }
    
    public static BuiltInType forJavaSize(int javaSize) {
        for (BuiltInType bit : BuiltInType.values()) {
            if (bit.getSize() == javaSize)
                return bit;
        }
        throw new IllegalArgumentException("No built-in type with Java size " + javaSize);
    }

    public static BuiltInType largestNumeric(IScope scope, BuiltInType t1, BuiltInType t2) {
        if (!t1.isNumeric() || !t2.isNumeric())
            throw new CompilationException(scope, "Types are not both numeric");
        if (t1.getSize() > t2.getSize()) {
            return t1;
        } else if (t1.getSize() < t2.getSize()) {
            return t2;
        } else if (t1.isNumeric()) {
            return t1;
        } else if (t2.isNumeric()) {
            return t2;
        } else if (t1.isSigned()) {
            return t1;
        } else {
            return t2;
        }
    }
    
    public static BuiltInType javaInfixNumeric(IScope scope, BuiltInType t1, BuiltInType t2) {
        BuiltInType result = largestNumeric(scope, t1, t2);
        if (result.size < BuiltInType.javaInt().size)
            return BuiltInType.javaInt(); // Java casts smaller types to "int" after an operation
        return result;
    }
    
    public static List<BuiltInType> getNumericTypes(Boolean decimal) {
        List<BuiltInType> result = new ArrayList<>();
        for (BuiltInType builtInType : BuiltInType.values()) {
            if (builtInType.isNumeric() && (decimal == null || decimal.equals(builtInType.isDecimal())))
                result.add(builtInType);
        }
        return result;
    }
    
    public static BuiltInType javaInt() {
        BuiltInType result = BuiltInType.LONGINT;
        assert result.getJavaType().equals("int");
        return result;
    }
    
}
