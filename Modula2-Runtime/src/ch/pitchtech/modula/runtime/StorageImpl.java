package ch.pitchtech.modula.runtime;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;

import ch.pitchtech.modula.runtime.Runtime.SizeOfData;

public class StorageImpl {

    public static Object allocate(int size) {
        SizeOfData sizeOfData = Runtime.popLastSizeOfData();
        if (sizeOfData == null) {
            throw new IllegalStateException("Cannot implement Storage.ALLOCATE because the type is not known."
                    + "\n  Make sure the Modula-2 code either uses NEW(),"
                    + "\n  or uses SIZE(...) or TSIZE(...) (possibly with a multiplication) as argument."
                    + "\nOr compile with pointer arithmetic support enabled.");
        }
        Class<?> type = sizeOfData.type();
        if (type != null) {
            // ALLOCATE(var a, SIZE(type)) was used
            if (type.isArray()) {
                int nbIndexes = 1;
                Class<?> elementType = type.getComponentType();
                while (elementType.isArray()) {
                    elementType = elementType.getComponentType();
                    nbIndexes++;
                }
                if (sizeOfData.dimensions() == null || sizeOfData.dimensions().length != nbIndexes)
                    throw new IllegalStateException("The number of dimensions does not match the type");
                Object[] result = (Object[]) Array.newInstance(elementType, sizeOfData.dimensions());
                instanciateArrayElements(result, elementType);
                return result;
            } else if (ManagedRecord.class.isAssignableFrom(type)) {
                // Managed RECORD
                try {
                    return type.getConstructor(int.class).newInstance(size);
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (type.isPrimitive()) {
                // Assume an array is desired. Typically "ALLOCATE(SIZE(type) * nbElements)"
                int nbElements = size / sizeOfData.calculatedSize();
                if (size % sizeOfData.calculatedSize() != 0)
                    throw new IllegalArgumentException();
                return Array.newInstance(type, nbElements);
            } else {
                // Regular RECORD
                try {
                    return type.getConstructor().newInstance();
                } catch (ReflectiveOperationException ex) {
                    // Try private
                    try {
                        Constructor<?> constructor = type.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        return constructor.newInstance();
                    } catch (ReflectiveOperationException ex2) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        } else {
            // ALLOCATE(var a, size) was used - no type specified
            return new Address(size);
        }
    }
    
    private static void instanciateArrayElements(Object[] array, Class<?> elementType) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof Object[] subArray) {
                instanciateArrayElements(subArray, elementType);
            } else if (array[i] == null) { // non null if primitive type
                assert array[i] == null;
                try {
                    Object item = null;
                    Constructor<?> constructor;
                    try {
                        constructor = elementType.getConstructor();
                    } catch (NoSuchMethodException ex) {
                        constructor = elementType.getDeclaredConstructor();
                        constructor.setAccessible(true);
                    }
                    item = constructor.newInstance();
                    array[i] = item;
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
    }

    public static Object deallocate(Object obj) {
        // It is assumed the compiler will produce the following code:
        // obj = Storage.deallocate(obj);
        return null;
    }

}
