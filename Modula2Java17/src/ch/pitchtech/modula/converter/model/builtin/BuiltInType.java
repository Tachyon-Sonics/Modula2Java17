package ch.pitchtech.modula.converter.model.builtin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
import ch.pitchtech.modula.converter.compiler.UnsignedType;
import ch.pitchtech.modula.converter.model.scope.IScope;

public enum BuiltInType {
    INTEGER(true, true, false),
    SHORTINT(true, true, false),
    LONGINT(true, true, false),
    CARDINAL(true, false, false),
    SHORTCARD(true, false, false),
    LONGCARD(true, false, false),
    REAL(4, 4, "float", "Float", true, true, true),
    LONGREAL(8, 8, "double", "Double", true, true, true),
    CHAR(1, 1, "char", "Character"),
    BOOLEAN(1, 1, "boolean", "Boolean"),
    BITSET(4, 2, "Runtime.RangeSet", "Runtime.RangeSet"),
    ADDRESS(8, 4, "Object", "Object"),
    PROC(8, 4, "Runnable", "Runnable"),
    BYTE(1, 1, "byte", "Byte"),
    WORD(2, 2, "short", "Short"),
    LONGWORD(4, 4, "int", "Integer"),
    STRING(8, 4, "String", "String"); // For constants only
    
    private final int javaSize;
    private final int modulaSize;
    private final String javaType;
    private final String boxedType;
    private final boolean numeric;
    private final boolean signed;
    private final boolean decimal;
    
    
    private BuiltInType(int javaSize, int modulaSize, String javaType, String boxedType) {
        this(javaSize, modulaSize, javaType, boxedType, false, false, false);
    }
    
    private BuiltInType(int javaSize, int modulaSize, String javaType, String boxedType, 
            boolean numeric, boolean signed, boolean decimal) {
        this.javaSize = javaSize;
        this.modulaSize = modulaSize;
        this.javaType = javaType;
        this.boxedType = boxedType;
        this.numeric = numeric;
        this.signed = signed;
        this.decimal = decimal;
    }
    
    private BuiltInType(boolean numeric, boolean signed, boolean decimal) {
        // Delegate those to current DataModelType
        this.javaSize = -1;
        this.modulaSize = -1;
        this.javaType = null;
        this.boxedType = null;
        
        this.numeric = numeric;
        this.signed = signed;
        this.decimal = decimal;
    }
    
    private static String getJavaType(int size) {
        return switch (size) {
            case 1 -> "byte";
            case 2 -> "short";
            case 4 -> "int";
            case 8 -> "long";
            default -> throw new IllegalArgumentException();
        };
    }
    
    private static String getBoxedType(int size) {
        return switch (size) {
            case 1 -> "Byte";
            case 2 -> "Short";
            case 4 -> "Integer";
            case 8 -> "Long";
            default -> throw new IllegalArgumentException();
        };
    }
    
    public int getJavaSize() {
        if (javaSize < 0) {
            DataModelType dataModel = CompilerOptions.get().getDataModel();
            return dataModel.getJavaSize(this);
        }
        return javaSize;
    }
    
    public int getModulaSize() {
        if (modulaSize < 0) {
            DataModelType dataModel = CompilerOptions.get().getDataModel();
            return dataModel.getModulaSize(this);
        }
        return modulaSize;
    }

    public String getJavaType() {
        if (javaType == null) {
            DataModelType dataModel = CompilerOptions.get().getDataModel();
            return getJavaType(dataModel.getJavaSize(this));
        }
        return javaType;
    }
    
    public String getBoxedType() {
        if (javaType == null) {
            DataModelType dataModel = CompilerOptions.get().getDataModel();
            return getBoxedType(dataModel.getJavaSize(this));
        }
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
    
    /**
     * Whether this is a numeric, non decimal, unsigned type.
     * <tt>true</tt> for <tt>SHORTCARD</tt>, <tt>CARDINAL</tt> and <tt>LONGCARD</tt>
     */
    public boolean isAnyCardinal() {
        return isNumeric() && !isDecimal() && !isSigned();
    }
    
    /**
     * Get as an {@link UnsignedType}, or <tt>null</tt> if this built-in type is not an unisgned type
     */
    public UnsignedType getUnsignedType() {
        if (!isAnyCardinal())
            return null;
        return UnsignedType.fromJava(getJavaType());
    }
    
    public static BuiltInType forJavaSize(int javaSize) {
        for (BuiltInType bit : BuiltInType.values()) {
            if (bit.getJavaSize() == javaSize)
                return bit;
        }
        throw new IllegalArgumentException("No built-in type with Java size " + javaSize);
    }

    public static BuiltInType largestNumeric(IScope scope, BuiltInType t1, BuiltInType t2) {
        if (!t1.isNumeric() || !t2.isNumeric())
            throw new CompilationException(scope, "Types are not both numeric");
        if (t1.getJavaSize() > t2.getJavaSize()) {
            return t1;
        } else if (t1.getJavaSize() < t2.getJavaSize()) {
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
        if (result.getJavaSize() < BuiltInType.getTypeForJavaInt().getJavaSize())
            return BuiltInType.getTypeForJavaInt(); // Java casts smaller types to "int" after an operation
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
    
    /**
     * The Built-in Modula-2 type that corresponds to Java's <tt>int</tt>.
     * <p>
     * Returned as a delayed {@link Supplier}, because the value depends on the {@link DataModelType}
     * specified in the {@link CompilerOptions}.
     */
    public static Supplier<BuiltInType> javaInt() {
        return () -> {
            BuiltInType result = CompilerOptions.get().getDataModel().getTypeForJavaInt();
            assert result.getJavaType().equals("int");
            return result;
        };
    }
    
    /**
     * The Built-in Modula-2 type that corresponds to Java's <tt>int</tt>.
     * <p>
     * This method should not be invoked before the compilation starts, as its return value
     * depends on the {@link DataModelType} specified in the {@link CompilerOptions}.
     */
    public static BuiltInType getTypeForJavaInt() {
        return javaInt().get();
    }
    
}
