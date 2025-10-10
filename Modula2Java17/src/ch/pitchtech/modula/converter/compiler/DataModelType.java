package ch.pitchtech.modula.converter.compiler;

import ch.pitchtech.modula.converter.cmd.IArgName;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;

/**
 * Data-model (16-bit or 32-bit INTEGER/CARDINAL)
 */
public enum DataModelType implements IArgName {
    /**
     * INTEGER and CARDINAL are 16-bit, LONGINT and LONGCARD are 32-bit.
     * <p>
     * All types (except BYTE) ar expanded to at least Java's <tt>int</tt>.
     * <p>
     * LONGINT and LONGCARD are expanded to Java's <tt>long</tt> for compatibility with
     * {@link #LOOSE_32_64}.
     * TODO (1): long/int constants should be down-casted anyway as required
     * TODO (1): Constants are long, should be int if it fits in an int; 
     * TODO (1): GrotteAction.WaitKey, ClearLine((int) 21) : type cast to int is not necessary
     */
    LOOSE_16_32("16", 16, BuiltInType.INTEGER) {

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
     * INTEGER and CARDINAL are 32-bit, LONGINT and LONGCARD are 64-bit.
     * <p>
     * All types (except BYTE) ar expanded to at least Java's <tt>int</tt>.
     * <p>
     * Note that CARDINAL is mapped to <tt>int</tt>. It will hence only hold
     * 31 bits, unless {@link CompilerOptions#isUnsignedCard32()} is <tt>true</tt>.
     * <p>
     * Similarly, LONGCARD will only hold 63 bits, unless {@link CompilerOptions#isUnsignedCard64()}.
     */
    LOOSE_32_64("32", 32, BuiltInType.INTEGER) {

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
     * INTEGER and CARDINAL are 16-bit, LONGINT and LONGCARD are 32-bit
     */
    STRICT_16_32("s16", 16, BuiltInType.LONGINT) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 1;
                case INTEGER -> 2;
                case LONGINT -> 4;
                case SHORTCARD -> 2;
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
     * INTEGER and CARDINAL are 32-bit, LONGINT is 64-bit, LONGCARD is 63-bit (uses <tt>long</tt>)
     */
    STRICT_32_64("s32", 32, BuiltInType.INTEGER) {

        @Override
        public int getJavaSize(BuiltInType type) {
            return switch (type) {
                case SHORTINT -> 2;
                case INTEGER -> 4;
                case LONGINT -> 8;
                case SHORTCARD -> 4;
                case CARDINAL -> 8;
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
    
    @Override
    public String getArgName() {
        return this.argName;
    }
    
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
