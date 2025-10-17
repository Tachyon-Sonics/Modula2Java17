package ch.pitchtech.modula.converter.compiler;

import ch.pitchtech.modula.converter.cmd.IArgName;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;

/**
 * Data-model (16-bit or 32-bit INTEGER/CARDINAL)
 */
public enum DataModelType implements IArgName {
    /**
     * 16-bit data model.
     * <p>
     * INTEGER and CARDINAL are 16-bit, LONGINT and LONGCARD are 32-bit.
     * <p>
     * All types (except BYTE) ar expanded to at least Java's <tt>int</tt>.
     * <p>
     * LONGINT and LONGCARD are expanded to Java's <tt>long</tt> for compatibility with
     * {@link #LOOSE_32_64}.
     */
    DM_16("16", 16, BuiltInType.INTEGER) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 4;
                case INTEGER -> 4;
                case LONGINT -> 8;
                case SHORTCARD -> 4;
                case CARDINAL -> 4;
                case LONGCARD -> 8;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }

        @Override
        public int getModulaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT, SHORTCARD -> 1;
                case INTEGER, CARDINAL -> 2;
                case LONGINT, LONGCARD -> 4;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }
    },
    /**
     * 32-bit data model (default).
     * <p>
     * INTEGER and CARDINAL are 32-bit, LONGINT and LONGCARD are 64-bit.
     * <p>
     * All types (except BYTE) ar expanded to at least Java's <tt>int</tt>.
     * <p>
     * CARDINAL is mapped to <tt>int</tt> and may hence result in code using
     * {@link Integer#compareUnsigned(int, int)} and similar. Same for LONGCARD.
     */
    DM_32("32", 32, BuiltInType.INTEGER) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 4;
                case INTEGER -> 4;
                case LONGINT -> 8;
                case SHORTCARD -> 4;
                case CARDINAL -> 4;
                case LONGCARD -> 8;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }

        @Override
        public int getModulaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT, SHORTCARD -> 2;
                case INTEGER, CARDINAL -> 4;
                case LONGINT, LONGCARD -> 8;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }
    },
    /**
     * Strict 16-bit model.
     * <p>
     * SHORTINT and SHORTCARD are 8-bit, INTEGER and CARDINAL are 16-bit, LONGINT and LONGCARD are 32-bit.
     * <p>
     * Unlike {@link #DM_16}, all numeric types are mapped to Java types of exactly the same size.
     */
    DM_STRICT_16("s16", 16, BuiltInType.LONGINT) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 1;
                case INTEGER -> 2;
                case LONGINT -> 4;
                case SHORTCARD -> 1;
                case CARDINAL -> 2;
                case LONGCARD -> 4;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }

        @Override
        public int getModulaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT, SHORTCARD -> 1;
                case INTEGER, CARDINAL -> 2;
                case LONGINT, LONGCARD -> 4;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }
    },
    /**
     * Strict 32-bit model.
     * <p>
     * SHORTINT and SHORTCARD are 16-bit, INTEGER and CARDINAL are 32-bit, LONGINT and LONGCARD are 64-bit.
     * <p>
     * Unlike {@link #DM_32}, all numeric types are mapped to Java types of exactly the same size.
     */
    DM_STRICT_32("s32", 32, BuiltInType.INTEGER) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 2;
                case INTEGER -> 4;
                case LONGINT -> 8;
                case SHORTCARD -> 2;
                case CARDINAL -> 4;
                case LONGCARD -> 8;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }

        @Override
        public int getModulaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT, SHORTCARD -> 2;
                case INTEGER, CARDINAL -> 4;
                case LONGINT, LONGCARD -> 8;
                default -> throw new IllegalArgumentException("Not an integer type: " + type);
            };
        }
    };
    
    private final String argName;
    private final int nbBits;
    private final BuiltInType typeForJavaInt;
    
    
    private DataModelType(String argName, int nbBits, BuiltInType typeForJavaInt) {
        this.argName = argName;
        this.nbBits = nbBits;
        this.typeForJavaInt = typeForJavaInt;
    }
    
    /**
     * Name of this data model when used in the command line
     */
    @Override
    public String getArgName() {
        return this.argName;
    }
    
    /**
     * The number of bits for the Modula-2 INTEGER, CARDINAL and WORD types, such as <tt>16</tt> or <tt>32</tt>
     */
    public int getNbBits() {
        return this.nbBits;
    }
    
    /**
     * Get the Modula-2 type that is the closest to Java's <tt>int</tt>
     */
    public BuiltInType getTypeForJavaInt() {
        return this.typeForJavaInt;
    }
    
    /**
     * Java size for the given signed or unsigned integer type
     * TODO (3) extend to WORD, BITSET, etc
     */
    public abstract int getJavaSize(BuiltInType type);
    
    /**
     * Modula-2 size for the given signed or unsigned integer type
     */
    public abstract int getModulaSize(BuiltInType type);
    
}
