package ch.pitchtech.modula.converter.compiler;


public enum UnsignedType {
    CARD8(byte.class),
    CARD16(short.class),
    CARD32(int.class),
    CARD64(long.class);
    
    private final Class<?> javaType;
    
    private UnsignedType(Class<?> javaType) {
        this.javaType = javaType;
    }

    /**
     * The Java signed type that has the same size
     */
    public Class<?> getJavaType() {
        return javaType;
    }
    
}
