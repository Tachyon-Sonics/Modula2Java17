package ch.pitchtech.modula.runtime;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

// TODO try gradle's access rules: https://discuss.gradle.org/t/buildship-1-0-18-is-now-available/19012
public class Runtime {
    
    public static interface IRef<T> {
        
        public void set(T value);
        
        public T get();
        
        /**
         * Get the underlying data type of this reference.
         * <p>
         * If this is a reference to a pointer/address, returns <tt>IRef.class</tt>.
         * <p>
         * For primitive types, the boxed type is returned (for example {@link Integer} instead of <tt>int</tt>)
         */
        @SuppressWarnings("unchecked")
        public default Class<T> getDataType() {
            T value = get();
            if (value == null) {
                // Only references to pointer/address/opaque can be null
                return (Class<T>) IRef.class;
            } else {
                return (Class<T>) value.getClass();
            }
        }
        
        @SuppressWarnings({ "unchecked" })
        public default void inc(Number delta) {
            T value = get();
            if (value instanceof Long longValue) {
                set((T) (Long) (longValue + delta.longValue()));
            } else if (value instanceof Integer intValue) {
                set((T) (Integer) (intValue + delta.intValue()));
            } else if (value instanceof Short shortValue) {
                set((T) (Short) (short) (shortValue + delta.shortValue()));
            } else if (value instanceof Byte byteValue) {
                set((T) (Byte) (byte) (byteValue + delta.byteValue()));
            } else if (value instanceof Character charValue) {
                set((T) (Character) (char) (charValue + delta.intValue()));
            } else if (value instanceof Double doubleValue) {
                set((T) (Double) (doubleValue + delta.doubleValue()));
            } else if (value instanceof Float floatValue) {
                set((T) (Float) (floatValue + delta.floatValue()));
            } else if (Enum.class.isInstance(value)) {
                Class<?> enumType = value.getClass();
                int ordinal = ((Enum<?>) value).ordinal();
                T result = (T) enumType.getEnumConstants()[ordinal + delta.intValue()];
                set(result);
            } else {
                throw new RuntimeException("Value in not numeric: " + value);
            }
        }
        
        public default void dec(Number delta) {
            if (delta instanceof Double || delta instanceof Float)
                inc(-delta.doubleValue());
            else
                inc(-delta.longValue());
        }
        
        public default void inc() {
            inc(1);
        }
        
        public default void dec() {
            inc(-1);
        }
        
        /**
         * Cast the IRef to an IRef of another type. Can only be used for numeric types.
         * <p>
         * This allows a LONGINT (long) for instance to be passed in a VAR INTEGER (int) argument
         */
        public default <E> IRef<E> as(Class<E> targetType) {
            return new CastedRef<>(this, targetType);
        }
        
        /**
         * Convert to an IRef&lt;Object&gt;. Used when we need to type cast from POINTER to ADDRESS
         * and both are by reference.
         */
        public default IRef<Object> asAdrRef() {
            return new AdrRef(this);
        }
        
        public default IRef<byte[]> asByteArray(int m2size) {
            return Runtime.asByteArray(this, m2size);
        }
        
    }
    
    public static class Ref<T> implements IRef<T> { // Only for PROCEDURE-local variables, fully replaces variable
        
        private T value;
        
        
        public Ref() {
            
        }
        
        public Ref(T val) {
            this.value = val;
        }
        
        public Ref(IRef<T> ref) {
            this.value = (ref == null ? null : ref.get());
        }
        
        @Override
        public void set(T val) {
            this.value = val;
        }
        
        @Override
        public T get() {
            return this.value;
        }

        @Override
        public String toString() {
            return "Ref [val=" + value + "]";
        }
        
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class AdrRef implements IRef<Object> {
        
        private final IRef target;

        
        public AdrRef(IRef target) {
            this.target = target;
        }

        @Override
        public void set(Object value) {
            target.set(value);
        }

        @Override
        public Object get() {
            return target.get();
        }
        
    }
    
    /**
     * {@link IRef} implementation used when using ADR() on a variable of a module
     * TODO compile option to generate Runtime.FieldRef's in the records/compilation units instead of on each use
     */
    public static class FieldRef<T> implements IRef<T> {
        
        private final Supplier<T> getter;
        private final Consumer<T> setter;
        
        
        /**
         * Create a reference to a field for addressing using ADR, or for passing as a VAR argument.
         * <p>
         * Used for both RECORD fields, and compilation unit variables.
         */
        public FieldRef(Supplier<T> getter, Consumer<T> setter) {
            this.getter = getter;
            this.setter = setter;
        }
        
        public Supplier<T> getGetter() {
            return getter;
        }
        
        public Consumer<T> getSetter() {
            return setter;
        }

        @Override
        public T get() {
            return getter.get();
        }
        
        @Override
        public void set(T value) {
            setter.accept(value);
        }
        
    }
    
    /**
     * TODO GraphTest.mod line 329 replace mx and my by x and y (LONGINT passed to VAR INTEGER argument). Fix and use this class
     * @param <S> Source type (must be numeric and boxed)
     * @param <T> Target type (must be numeric and boxed)
     */
    private static class CastedRef<S, T> implements IRef<T> {
        
        private final IRef<S> source;
        private final Class<S> sourceType;
        private final Class<T> targetType; 
        
        
        @SuppressWarnings("unchecked")
        public CastedRef(IRef<S> source, Class<T> targetType) {
            this.source = source;
            this.sourceType = (Class<S>) source.get().getClass();
            this.targetType = targetType;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public void set(T value0) {
            Number value = (Number) value0;
            if (sourceType.equals(Byte.class))
                source.set((S) (Byte) value.byteValue());
            else if (sourceType.equals(Short.class))
                source.set((S) (Short) value.shortValue());
            else if (sourceType.equals(Integer.class))
                source.set((S) (Integer) value.intValue());
            else if (sourceType.equals(Long.class))
                source.set((S) (Long) value.longValue());
            else if (sourceType.equals(Float.class))
                source.set((S) (Float) value.floatValue());
            else if (sourceType.equals(Double.class))
                source.set((S) (Double) value.doubleValue());
        }

        @SuppressWarnings("unchecked")
        @Override
        public T get() {
            Number value = (Number) source.get();
            if (targetType.equals(Byte.class))
                return (T) (Byte) value.byteValue();
            else if (targetType.equals(Short.class))
                return (T) (Short) value.shortValue();
            else if (targetType.equals(Integer.class))
                return (T) (Integer) value.intValue();
            else if (targetType.equals(Long.class))
                return (T) (Long) value.longValue();
            else if (targetType.equals(Float.class))
                return (T) (Float) value.floatValue();
            else if (targetType.equals(Double.class))
                return (T) (Double) value.doubleValue();
            throw new IllegalStateException();
        }
        
    }
    
    /**
     * {@link IRef} implementation used when using ADR() on the field of a record.
     * <p>
     * The record is evaluated only once (it can be a complex expression) and stored
     * in this {@link FieldExprRef}.
     */
    public static class FieldExprRef<E, T> implements IRef<T> {
        
        private final E expressionValue;
        private final Function<E, T> getter;
        private final BiConsumer<E, T> setter;
        
        
        /**
         * Create a reference to a field for addressing using ADR, or for passing as a VAR argument.
         * <p>
         * Generally used with WITH statements on a complex expression.
         */
        public FieldExprRef(E expressionValue, Function<E, T> getter, BiConsumer<E, T> setter) {
            this.expressionValue = expressionValue;
            this.getter = getter;
            this.setter = setter;
        }
        
        public E getExpressionValue() {
            return expressionValue;
        }
        
        public Function<E, T> getGetter() {
            return getter;
        }
        
        public BiConsumer<E, T> getSetter() {
            return setter;
        }

        @Override
        public void set(T value) {
            setter.accept(expressionValue, value);
        }

        @Override
        public T get() {
            return getter.apply(expressionValue);
        }
        
    }
    
    /**
     * {@link IRef} implementation used when using ADR() on an array element, such as <tt>ADR(myArray[myIndex])</tt>
     */
    public static class ArrayElementRef<T> implements IRef<T> {
        /*
         * For a bidimentional array such as "int[][] matrix", use
         * "new ArrayElementRef(matrix[index1], index2)"
         */
        
        private final Object array;
        private final int index;
        
        
        /**
         * Create a reference to an array element, for addressing with ADR, or for passing as a VAR argument
         * @param array the target array
         * @param index the array's index. For multidimensional arrays, this is the last index, and '<tt>array</tt>' contains the
         * head index(es).
         */
        public ArrayElementRef(Object array, int index) {
            if (!(array.getClass().isArray()))
                throw new IllegalArgumentException("Not an array: " + array);
            this.array = array;
            this.index = index;
        }
        
        public Object getArray() {
            return array;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public void set(T value) {
            Array.set(array, index, value);
        }

        @Override
        public T get() {
            @SuppressWarnings("unchecked")
            T result = (T) Array.get(array, index);
            return result;
        }
        
    }
    
    /**
     * Definition of a "[lower .. upper]" range type. Also used (with "_r" suffix) for
     * "SET OF [lower .. upper]".
     * @param lower lower bound, inclusive
     * @param upper upper bound, inclusive
     */
    public static record Range(int lower, int upper) {}
    
    /**
     * <tt>SET OF [&lt;lower&gt;..&lt;upper&gt;]</tt>
     * <p>
     * The lower and upper bounds are checked on every access.
     */
    public static class RangeSet implements Cloneable {
        
        private final int lower;
        private final int upper;
        private final BitSet target;
        
        
        /**
         * Create a new empty SET with the given subrange type
         * @param bounds the subrange type, providing the lower and upper bounds for the SET
         */
        public RangeSet(Range bounds) {
            this(bounds.lower(), bounds.upper());
        }
        
        /**
         * Create a new empty SET
         * @param lower lower bound, inclusive
         * @param upper upper bound, inclusive
         */
        public RangeSet(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
            this.target = new BitSet(upper - lower + 1);
        }
        
        public int getLower() {
            return lower;
        }
        
        public int getUpper() {
            return upper;
        }

        public BitSet getTarget() {
            return target;
        }

        public void copyFrom(RangeSet other) {
            this.target.clear();
            this.target.or(other.target);
        }
        
        public void incl(int index) {
            check(index);
            target.set(index - lower);
        }
        
        public void excl(int index) {
            check(index);
            target.clear(index - lower);
        }
        
        /**
         * Include (set) the given indexes
         * @return <tt>this</tt>
         */
        public RangeSet with(int... indexes) {
            for (int index : indexes)
                incl(index);
            return this;
        }
        
        /**
         * Include (set) all the indexes between the two bounds
         * @param lower lower bound, inclusive
         * @param upper upper bound, inclusive
         * @return <tt>this</tt>
         */
        public RangeSet withRange(int lower, int upper) {
            for (int index = lower; index <= upper; index++)
                incl(index);
            return this;
        }
        
        public boolean contains(int index) {
            check(index);
            return target.get(index - lower);
        }

        private void check(int index) {
            if (index < lower || index > upper)
                throw new ArrayIndexOutOfBoundsException("Index " + index + " is outside of bounds [" + lower + ".." + upper + "]");
        }

        public static RangeSet plus(RangeSet a, RangeSet b) {
            RangeSet result = a.clone();
            result.target.or(b.target);
            return result;
        }
        
        public static RangeSet minus(RangeSet a, RangeSet b) {
            RangeSet result = a.clone();
            result.target.andNot(b.target);
            return result;
        }
        
        public static RangeSet mul(RangeSet a, RangeSet b) {
            RangeSet result = a.clone();
            result.target.and(b.target);
            return result;
        }
        
        public static RangeSet div(RangeSet a, RangeSet b) {
            RangeSet result = a.clone();
            result.target.xor(b.target);
            return result;
        }
        
        public short toWord() {
            short result = 0;
            byte[] data = target.toByteArray();
            if (data.length > 0)
                result += data[0];
            if (data.length > 1)
                result += ((short) data[1] << 8);
            return result;
        }
        
        public void fromWord(short word) {
            target.clear();
            target.and(BitSet.valueOf(new byte[] {
                    (byte) (word & 0xFF),
                    (byte) ((word >>> 8) & 0xFF)
            }));
        }
        
        @Override
        public RangeSet clone() {
            RangeSet result = new RangeSet(lower, upper);
            result.copyFrom(this);
            return result;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(lower, target, upper);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            RangeSet other = (RangeSet) obj;
            return lower == other.lower && Objects.equals(target, other.target) && upper == other.upper;
        }

        @Override
        public String toString() {
            return "RangeSet [lower=" + lower + ", upper=" + upper + ", target=" + target + "]";
        }
        
    }

    /**
     * Create a copy of the given {@link EnumSet}, in which all elements between <tt>lower</tt>
     * and <tt>upper</tt> (both inclusive) are set.
     */
    public static <E extends Enum<E>> EnumSet<E> withRange(EnumSet<E> enumSet, E lower, E upper) {
        EnumSet<E> result = enumSet.clone();
        result.addAll(EnumSet.range(lower, upper));
        return result;
    }
    

    /**
     * Helper annotation for fields of generated Java classes corresponding to a RECORD.
     * <p>
     * Used by {@link Runtime.Record#copyFrom(ch.chaos.Runtime.Record)}.
     * <p>
     * Indicates that the given field is a nested RECORD (and not a POINTER TO RECORD), meaning that its content must be copied
     * and not just its reference, when copying the containing record.
     */
    @Retention(RUNTIME)
    @Target(FIELD)
    public @interface NestedRecord {

    }
    
    /**
     * Helper annotation for fields of generated Java classes corresponding to an ARRAY nested in a RECORD.
     * <p>
     * Used by {@link Runtime.Record#copyFrom(ch.chaos.Runtime.Record)}.
     * <p>
     * Indicates that the given field is a nested ARRAY (and not a POINTER TO ARRAY), meaning that its content must be copied
     * and not just its reference, when copying the containing record.
     * <p>
     * Dimensions are used by {@link Runtime#sizeOf(Class, int...)}
     */
    public @interface NestedArray {
        public int[] dimensions();
    }

    public static class Record<T extends Record<T>> {
        
        public void copyFrom(Record<T> other) {
            copyFromImpl(other);
        }
        
        private void copyFromImpl(Object other) {
            for (Field field : getClass().getDeclaredFields()) {
                NestedRecord nestedRecord = field.getAnnotation(NestedRecord.class); // We cannot use "instanceof Record" because a POINTER TO RECORD will also show a record
                try {
                    if (nestedRecord != null) {
                        // This is a nested RECORD. Recursively copy its content:
                        Record<?> otherRecord = (Record<?>) field.get(other);
                        Record<?> thisRecord = (Record<?>) field.get(this);
                        thisRecord.copyFromImpl(otherRecord);
                    } else {
                        field.set(this, field.get(other));
                    }
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        
        @Override
        public boolean equals(Object other0) {
            if (!getClass().equals(other0.getClass()))
                return false;
            Record<?> other = (Record<?>) other0;
            for (Field field : getClass().getDeclaredFields()) {
                try {
                    Object thisValue = field.get(this);
                    Object otherValue = field.get(other);
                    if (Record.class.isAssignableFrom(field.getType())) {
                        NestedRecord nestedRecord = field.getAnnotation(NestedRecord.class);
                        if (nestedRecord == null) {
                            // This is a pointer. Compare references:
                            if (thisValue != otherValue)
                                return false;
                        } else {
                            // This is a nested record. Compare content
                            if (!Objects.equals(thisValue, otherValue))
                                return false;
                        }
                    } else {
                        if (!Objects.equals(thisValue, otherValue))
                            return false;
                    }
                    
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return true;
        }
        
        @SuppressWarnings("unchecked")
        public T newCopy() {
            try {
                T copy = (T) getClass().getConstructor().newInstance();
                copy.copyFrom((T) this);
                return copy;
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * When SIZE() or TSIZE() is invoked, additional information is stored, in addition to the returned value.
     * <p>
     * That information can be retrieved by {@link Runtime#popLastSizeOfData()}. It contains the returned value,
     * the Class on which SIZE/TSIZE was invoked, and if the argument was an array, the dimensions of the array.
     * @param calculatedSize the size of the object or type, as it was returned by {@link Runtime#sizeOf(int, Class, int...)}
     * @param type the type / type of the object on which it was invoked
     * @param dimensions if the type is an array, the dimension(s) of the array. This attribute is undefined if the type is not an array.
     */
    public static record SizeOfData(int calculatedSize, Class<?> type, int[] dimensions) {}
    
    private static ThreadLocal<SizeOfData> lastSizeOf = new ThreadLocal<>();
    
    /**
     * Support for SIZE and TSIZE. The result is known at compile time; however, most of the time the result
     * of SIZE/TSIZE is used in Storage.ALLOCATE(). Hence the translator generates a call to this method,
     * with the calculated size (it will be returned) and the corresponding Java type. The Java type is stored,
     * can be retrieved by {@link #popLastSizeOfData()}, and can be used to instanciate the corresponding Java
     * class. This is how {@link StorageImpl} works.
     * @param arraySizes if this is an array type, the dimensions, so that a pre-allocated array can be instanciated
     */
    public static int sizeOf(int calculatedSize, Class<?> clazz, int... arraySizes) {
        lastSizeOf.set(new SizeOfData(calculatedSize, clazz, arraySizes));
        return calculatedSize;
    }
    
    /**
     * When a <tt>SIZE(...)</tt> or TSIZE(...)</tt> expression is converted to Java, the size is returned as an int,
     * but details about the underlying data type are also stored in a {@link ThreadLocal}.
     * <p>
     * This method retrives and clear that value from the {@link ThreadLocal}
     * @return data type information of the last call to <tt>SIZE(...)</tt> or TSIZE(...)</tt>, or
     * <tt>null</tt> if none of the functions were used.
     */
    public static SizeOfData popLastSizeOfData() {
        SizeOfData result = lastSizeOf.get();
        lastSizeOf.remove();
        return result;
    }
    
    public static <E extends Enum<E>> EnumSet<E> plusSet(EnumSet<E> a, EnumSet<E> b) {
        EnumSet<E> result = a.clone();
        result.addAll(b);
        return result;
    }
    
    public static <E extends Enum<E>> EnumSet<E> minusSet(EnumSet<E> a, EnumSet<E> b) {
        EnumSet<E> result = a.clone();
        result.removeAll(b);
        return result;
    }
    
    public static <E extends Enum<E>> EnumSet<E> mulSet(EnumSet<E> a, EnumSet<E> b) {
        EnumSet<E> result = a.clone();
        result.retainAll(b);
        return result;
    }
    
    public static <E extends Enum<E>> EnumSet<E> divSet(EnumSet<E> a, EnumSet<E> b) {
        EnumSet<E> result = a.clone();
        result.addAll(b);
        result.removeAll(mulSet(a, b));
        return result;
    }
    
    public static Object plusAdr(Object a, Object b) {
        throw new UnsupportedOperationException("Pointer arithmetic was not enabled during compilation");
    }
    
    public static Object minusAdr(Object a, Object b) {
        throw new UnsupportedOperationException("Pointer arithmetic was not enabled during compilation");
    }
    
    // Used for "pointer:= ADDRESS"
    /**
     * Convert an Object (that typically was an ADDRESS) to an {@link IRef} of type '<tt>refType</tt>'
     * (typically a POINTER TO <tt>refType</tt>).
     * <p>
     * The value might be a plain {@link String} if ADR("Constant String Literal") was used, in that
     * case it is just wrapped in a {@link Ref}.
     */
    @SuppressWarnings("unchecked")
    public static <E> IRef<E> castToRef(Object value, Class<E> refType) {
        if (value instanceof String text && refType.equals(String.class)) {
            // Occurs when ADR("Constant String Literal") is used
            return (IRef<E>) new Ref<String>(text);
        }
        IRef<E> result = (IRef<E>) value;
        return result;
    }
    
    /**
     * An argument of type "ARRAY OF BYTE" is compatible with anything. This method implements the conversion of anything
     * to an array of byte.
     * <p>
     * The resulting byte array contains the value, stored according to the size of the Modula-2 types
     */
    public static byte[] toByteArray(Object item, int m2size) {
        ByteBuffer byteBuffer = toByteBuffer(item, m2size);
        if (byteBuffer.capacity() > m2size) {
            /*
             * The Modula-2 size is smaller than the Java size.
             * Crop to the Modula-2 size, obeying big-endian storage order.
             */
            byteBuffer = byteBuffer.slice(byteBuffer.capacity() - m2size, m2size);
        } else {
            byteBuffer.position(0);
        }
        byte[] result = new byte[m2size];
        byteBuffer.get(result);
        return result;
    }

    /**
     * Convert an arbitrary primitive value or array to a {@link ByteBuffer}.
     * <p>
     * This method uses the Java size, except for arrays that recurse to {@link #toByteArray(Object, int)}.
     * The caller, usually {@link #toByteArray(Object, int)} then crops to the Modula-2 size.
     * <p>
     * The {@link ByteBuffer} uses big-endian ordering.
     * <p>
     * {@link Character} and {@link Boolean} are converted to a single byte.
     */
    private static ByteBuffer toByteBuffer(Object item, int m2size) {
        if (item instanceof byte[] bArray) {
            return ByteBuffer.wrap(bArray);
        } else if (item instanceof Long number) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putLong(number);
            return buffer;
        } else if (item instanceof Integer number) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(number);
            return buffer;
        } else if (item instanceof Short number) {
            ByteBuffer buffer = ByteBuffer.allocate(2);
            buffer.putShort(number);
            return buffer;
        } else if (item instanceof Byte number) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(number);
            return buffer;
        } else if (item instanceof Character ch) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte) (char) ch);
            return buffer;
        } else if (item instanceof Boolean b) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte) (b ? -1 : 0));
            return buffer;
        } else if (item instanceof Double number) {
            ByteBuffer buffer = ByteBuffer.allocate(8);
            buffer.putDouble(number);
            return buffer;
        } else if (item instanceof Float number) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putFloat(number);
            return buffer;
        } else if (item instanceof Enum<?> enumItem) {
            ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put((byte) enumItem.ordinal());
            return buffer;
        } else if (item instanceof String string) {
            byte[] result = string.getBytes(StandardCharsets.UTF_8);
            byte[] padded = Arrays.copyOf(result, result.length + 1); // Add zero-padding
            return ByteBuffer.wrap(padded);
        } else if (item.getClass().isArray()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            try {
                int length = Array.getLength(item);
                for (int i = 0; i < length; i++) {
                    Object element = Array.get(item, i);
                    byte[] elementBytes = toByteArray(element, m2size / length);
                    result.write(elementBytes);
                }
                result.flush();
            } catch (IOException ex) { // Should not happen
                throw new RuntimeException(ex);
            }
            return ByteBuffer.wrap(result.toByteArray());
        } else {
            throw new UnsupportedOperationException(String.valueOf(item));
        }
    }

    public static Object fromByteBuffer(ByteBuffer buffer, Class<?> expectedType) {
        if (byte[].class.equals(expectedType)) {
            return buffer.array();
        } else if (Long.class.equals(expectedType) || long.class.equals(expectedType)) {
            return buffer.getLong();
        } else if (Integer.class.equals(expectedType) || int.class.equals(expectedType)) {
            return buffer.getInt();
        } else if (Short.class.equals(expectedType) || short.class.equals(expectedType)) {
            return buffer.getShort();
        } else if (Byte.class.equals(expectedType) || byte.class.equals(expectedType)) {
            return buffer.get();
        } else if (Character.class.equals(expectedType) || char.class.equals(expectedType)) {
            return (char) ((int) buffer.get() & 0xff);
        } else if (Boolean.class.equals(expectedType) || boolean.class.equals(expectedType)) {
            byte data = buffer.get();
            return (data != 0);
        } else if (Double.class.equals(expectedType) || double.class.equals(expectedType)) {
            return buffer.getDouble();
        } else if (Float.class.equals(expectedType) || float.class.equals(expectedType)) {
            return buffer.getFloat();
        } else if (expectedType.isEnum()) {
            int index = buffer.get();
            return expectedType.getEnumConstants()[index];
        } else if (String.class.equals(expectedType)) {
            byte[] padded = buffer.array();
            byte[] result = Arrays.copyOf(padded, padded.length - 1); // Remove zero-padding
            return new String(result, StandardCharsets.UTF_8);
        } else {
            throw new UnsupportedOperationException(expectedType.toString());
        }
    }

    /**
     * Return a <tt>byte[]</tt> {@link IRef} view of the given reference to an arbitrary value
     * (primitive type or array). Changing the array in the retured ref will affect the value,
     * as long as mutations are performed using the setter {@link IRef#set(Object)}.
     * <p>
     * This is used to convert anything to a <tt>VAR</tt> argument of type <tt>ARRAY OF CHAR</tt>.
     */
    public static IRef<byte[]> asByteArray(IRef<?> itemRef, int m2size) {
        Object value = itemRef.get();
        Class<?> type = value.getClass();
        byte[] data0 = toByteArray(value, m2size);
        return new IRef<byte[]>() {

            private byte[] data = data0;
            
            @SuppressWarnings("unchecked")
            @Override
            public void set(byte[] bArr) {
                this.data = bArr;
                int javaSize = JavaTypesHelper.getSize(type);
                if (javaSize > 0 && javaSize > m2size) {
                    /*
                     * The Modula-2 size is smaller than the Java size. Expand
                     * the buffer, obeying big-ending ordering
                     */
                    byte[] temp = new byte[javaSize];
                    System.arraycopy(bArr, 0, temp, javaSize - m2size, m2size);
                    bArr = temp;
                }
                Object newValue = fromByteBuffer(ByteBuffer.wrap(bArr), type);
                ((IRef<Object>) itemRef).set(newValue);
            }

            @Override
            public byte[] get() {
                return data;
            }
        };
    }
    
    /**
     * Get the next enum member. Support for "INC(enumerated)"
     * @param <E> the enum type
     * @param item the current enum member
     */
    public static <E extends Enum<E>> E next(E item) {
        return next(item, 1);
    }
    
    public static <E extends Enum<E>> E next(E item, int delta) {
        @SuppressWarnings("unchecked")
        Class<E> enumClass = (Class<E>) item.getClass();
        // Simplifies "var = EnumType.values()[var.ordinal() + 1]"
        return enumClass.getEnumConstants()[item.ordinal() + delta];
    }
    
    public static <E extends Enum<E>> E prev(E item) {
        return prev(item, 1);
    }
    
    public static <E extends Enum<E>> E prev(E item, int delta) {
        @SuppressWarnings("unchecked")
        Class<E> enumClass = (Class<E>) item.getClass();
        // Simplifies "var = EnumType.values()[var.ordinal() - 1]"
        return enumClass.getEnumConstants()[item.ordinal() - delta];
    }
    
    /**
     * Convert boolean to an int. For arrays of BOOLEAN
     */
    public static int ord(boolean value) {
        return value ? 1 : 0;
    }
    
    /**
     * Support for "charArray[x] := value" when char array are converted to Java Strings
     */
    public static String setChar(String text, int index, char value) {
        if (value == (char) 0) {
            // Crop to given position
            if (text.length() < index)
                return text;
            else
                return text.substring(0, index);
        } else {
            // Expand existing String
            while (index > text.length())
                text = text + (char) 0;
            // Set char
            if (index == text.length()) {
                return text + value;
            } else {
                return text.substring(0, index) + value + text.substring(index + 1);
            }
        }
    }
    
    /**
     * Same as {@link #setChar(String, int, char)} for the case in which the char array is by ref
     */
    public static void setChar(IRef<String> text, int index, char value) {
        text.set(setChar(text.get(), index, value));
    }
    
    /**
     * Get the char at the given index, returning 0 if past the String length, in order to properly simulate
     * null-terminated Strings
     */
    public static char getChar(String text, int index) {
        if (index >= text.length())
            return (char) 0;
        return text.charAt(index);
    }
    
    public static char getChar(IRef<String> text, int index) {
        return getChar(text.get(), index);
    }
    
    /**
     * Enclidean division. Support for "DIV" operator on INTEGER
     */
    public static int eDiv(int x, int y) {
        int r = Math.floorDiv(x, y);
        // if the divisor is negative and modulo not zero, round up
        if (y < 0 && r * y != x) {
            r++;
        }
        return r;        
    }
    
    /**
     * Enclidean modulo. Support for "MOD" operator on INTEGER
     */
    public static int eMod(int x, int y) {
        int r = x - eDiv(x, y) * y;
        return r;
    }
    
    /**
     * Enclidean division. Support for "DIV" operator on LONGINT
     */
    public static long eDiv(long x, long y) {
        long r = Math.floorDiv(x, y);
        // if the divisor is negative and modulo not zero, round up
        if (y < 0 && r * y != x) {
            r++;
        }
        return r;        
    }
    
    /**
     * Enclidean modulo. Support for "MOD" operator on LONGINT
     */
    public static long eMod(long x, long y) {
        long r = x - eDiv(x, y) * y;
        return r;
    }
    
    /**
     * Duplicate an array. This is used when assigning an array to another one, or when passing an array as a non-VAR argument
     * @param deep whether to duplicate elements. <tt>false</tt> for an ARRAY OF POINTER
     * @param array the array to duplicate
     */
    public static <E> E copyOf(boolean deep, E array) {
        Class<?> type = array.getClass();
        if (type.equals(String.class)) {
            // ARRAY OF CHAR promoted to String
            // String is immutable, hence we can safely return the original one without copying it
            return array;
        }
        
        if (!type.isArray())
            throw new IllegalArgumentException("Argument is not an array");
        Class<?> componentType = type.getComponentType();
        int length = Array.getLength(array);
        Object result = Array.newInstance(componentType, length);
        for (int i = 0; i < length; i++) {
            Object item = Array.get(array, i);
            if (deep) {
                if (item != null && item.getClass().isArray()) {
                    item = copyOf(deep, item); // Deep copy array
                } else if (item != null && !componentType.isPrimitive()) {
                    // Check for a newCopy() method indicating a RECORD, and use it to create a copy
                    try {
                        Method newCopy = item.getClass().getMethod("newCopy");
                        if (newCopy != null)
                            item = newCopy.invoke(item);
                    } catch (ReflectiveOperationException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            Array.set(result, i, item);
        }
        
        @SuppressWarnings("unchecked")
        E result0 = (E) result;
        return result0;
    }
    
    public static <E, T> E initArray(E array) {
        return initArray(array, null);
    }
    
    public static <E, T> E initArray(E array, Supplier<T> constructor) {
        if (!array.getClass().isArray())
            throw new IllegalArgumentException("Argument is not an array");
        Object[] array0 = (Object[]) array;
        initArray(array0, constructor);
        return array;
    }
    
    private static <T> void initArray(Object[] array, Supplier<T> constructor) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) {
                if (constructor == null) {
                    Class<?> elementType = array.getClass().getComponentType();
                    constructor = getDefaultConstructorFor(elementType);
                }
                array[i] = constructor.get();
            } else if (array[i].getClass().isArray()) {
                Object[] subArray = (Object[]) array[i];
                initArray(subArray, constructor);
            }
        }
    }

    private static <T> Supplier<T> getDefaultConstructorFor(Class<?> elementType) {
        if (elementType.isEnum()) {
            Object[] enumConstants = elementType.getEnumConstants();
            @SuppressWarnings("unchecked")
            T result = (T) enumConstants[0];
            return () -> result;
        }
        
        // Use no-arg constructor
        try {
            Constructor<?> newMethod = elementType.getConstructor();
            return constructorSupplier(newMethod);
        } catch (ReflectiveOperationException ex) {
            // Try private
            try {
                Constructor<?> newMethod = elementType.getDeclaredConstructor();
                newMethod.setAccessible(true);
                return constructorSupplier(newMethod);
            } catch (ReflectiveOperationException ex2) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static <T> Supplier<T> constructorSupplier(Constructor<?> newMethod) {
        return () -> {
            try {
                @SuppressWarnings("unchecked")
                T result = (T) newMethod.newInstance();
                return result;
            } catch (ReflectiveOperationException ex) {
                throw new RuntimeException(ex);
            }
        };
    }
    
    
    private final static Map<String, Object> procedureMap = new ConcurrentHashMap<>();
    
    
    /**
     * Support for comparison of variables of type PROCEDURE.
     * <p>
     * Indeed, in Java, <tt>this::myMethod != this::myMethod</tt> because each occurrence of
     * <tt>this::myMethod</tt> creates another lambda.
     * <p>
     * This method uses a compiler-generated key-name as a key to ensure uniqueness of method references.
     * The first lambda with a given key-name is stored in a Map, and then a subsequent lambda with the
     * same key-name is replaced by the existing lambda from the Map.
     * <p>
     * The key-name consists of the procedure name (given as argument) and the procedure type (discovered
     * using reflection)
     * <p>
     * Another approach could be to subclass the interface implemented by the given target (using <tt>Proxy</tt>)
     * and to implement equals.
     */
    public static <E> E proc(E target, String name) {
        String keyName = name;
        Class<?> targetType = target.getClass();
        Class<?>[] interfaces = targetType.getInterfaces(); // Normally only one, corresponding to the PROCEDURE type
        for (Class<?> intf : interfaces) {
            String intfName = intf.getName();
            if (intfName.contains("$")) { // ModuleName$InterfaceName, corresponds to PROCEDURE type
                String procTypeName = intfName.substring(intfName.lastIndexOf('.') + 1);
                keyName += "/" + procTypeName;
            }
        }
        // Now 'keyName' should be like: Module1.Procedure/Module2$ProcedureType
        
        /*
         * Question, does Modula-2 allow assignment/comparison between ProcedureType1
         * and ProcedureType2 if they have the same signature? If yes, we may want to
         * replace 'Module2$ProcedureType' by the signature...
         * But I don't think so.
         */
        
        Object previous = procedureMap.putIfAbsent(keyName, target);
        if (previous != null) {
            @SuppressWarnings("unchecked")
            E result = (E) previous;
            return result;
        }
        return target;
    }
    
    private static String appName;
    private static Object[] args;

    
    public static String getAppName() {
        return appName;
    }

    public static void setAppName(String appName) {
        Runtime.appName = appName;
    }
    
    public static String getAppNameOrDefault() {
        if (appName != null)
            return appName;
        String name = System.getProperty("sun.java.command");
        if (name == null || name.isBlank()) {
            return null;
        }
        if (name.endsWith(".jar")) {
            name = name.substring(0, name.length() - ".jar".length());
        }
        name = name.substring(name.lastIndexOf('.') + 1);
        name = name.substring(name.lastIndexOf('/') + 1);
        name = name.substring(name.lastIndexOf('\\') + 1);
        return name;
    }
    
    public static Object[] getArgs() {
        return args;
    }

    public static void setArgs(Object[] args) {
        Runtime.args = args;
    }
    
}
