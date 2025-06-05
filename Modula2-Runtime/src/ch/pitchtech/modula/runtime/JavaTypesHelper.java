package ch.pitchtech.modula.runtime;


public class JavaTypesHelper {
    
    /**
     * Get the Java size of the given primitive type, or <tt>-1</tt> if not a primitive type.
     * <p>
     * Note: char and boolean returns 0 because we explicitely store them as a byte
     */
    public static int getSize(Class<?> primitiveType) {
        if (primitiveType.equals(byte.class) || primitiveType.equals(Byte.class))
            return 1;
        else if (primitiveType.equals(short.class) || primitiveType.equals(Short.class))
            return 2;
        else if (primitiveType.equals(int.class) || primitiveType.equals(Integer.class))
            return 4;
        else if (primitiveType.equals(long.class) || primitiveType.equals(Long.class))
            return 8;
        else if (primitiveType.equals(float.class) || primitiveType.equals(Float.class))
            return 4;
        else if (primitiveType.equals(double.class) || primitiveType.equals(Double.class))
            return 8;
        else if (primitiveType.equals(char.class) || primitiveType.equals(Character.class))
            return 1; // This is not the real Java size, but the one we use for UTF-8 storage
        else if (primitiveType.equals(boolean.class) || primitiveType.equals(Boolean.class))
            return 1; // This is not the real Java size, but the one we use (we store a 0/-1 byte)
        return -1;
    }

}
