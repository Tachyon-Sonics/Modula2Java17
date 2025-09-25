package ch.pitchtech.modula.converter.compiler;

import ch.pitchtech.modula.converter.model.builtin.BuiltInType;

/**
 * Data-model (16-bit or 32-bit INTEGER/CARDINAL)
 */
public enum DataModelType {
    /**
     * INTEGER is 16-bit, LONGINT is 32-bit
     */
    INT_16_BIT {

        @Override
        public int getNbBits() {
            return 16;
        }

        @Override
        public BuiltInType getTypeForJavaInt() {
            return BuiltInType.LONGINT;
        }

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
     * INTEGER is 32-bit, LONGINT is 64-bit
     */
    INT_32_BIT {

        @Override
        public int getNbBits() {
            return 32;
        }

        @Override
        public BuiltInType getTypeForJavaInt() {
            return BuiltInType.INTEGER;
        }

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
    
    public abstract int getNbBits();
    
    /**
     * Get the Modula-2 type that is the closest to Java's <tt>int</tt>
     */
    public abstract BuiltInType getTypeForJavaInt();
    
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
