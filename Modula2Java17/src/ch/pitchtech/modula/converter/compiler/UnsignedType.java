package ch.pitchtech.modula.converter.compiler;


public enum UnsignedType {
    CARD8("8", byte.class),
    CARD16("16", short.class),
    CARD32("32", int.class),
    CARD64("64", long.class);
    
    private final String name;
    private final Class<?> javaType;
    
    private UnsignedType(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
    }
    
    public String getName() {
        return name;
    }

    /**
     * The Java signed primitive type that has the same size
     */
    public Class<?> getJavaType() {
        return javaType;
    }
    
    public static UnsignedType fromJava(String primitiveJavaClassName) {
        for (UnsignedType value : UnsignedType.values()) {
            if (value.getJavaType().getName().equals(primitiveJavaClassName)) {
                return value;
            }
        }
        return null;
    }
    
    public static UnsignedType fromName(String name) {
        for (UnsignedType value : UnsignedType.values()) {
            if (value.getName().equals(name))
                return value;
        }
        return null;
    }
    
}
