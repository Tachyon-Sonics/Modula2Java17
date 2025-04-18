package ch.pitchtech.modula.runtime;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * ADDRESS value that is the result of pointer-arithmetic
 */
public class Address {
    
    protected ByteBuffer target;
    protected int offset;
    protected int length;

    
    /**
     * Used when created by {@link StorageImpl#allocate(int)} or NEW()
     */
    public Address(int size) {
        this.target = ByteBuffer.allocate(size);
        this.target.order(ByteOrder.nativeOrder());
        this.offset = 0;
        this.length = size;
    }
    
    /**
     * Used by sub-types when type-casting from an ADDRESS/POINTER
     */
    protected Address(Address address) {
        this.target = address.target;
        this.offset = address.offset;
        this.length = address.length;
    }
    
    private Address(ByteBuffer target, int offset, int length) {
        this.target = target;
        this.offset = offset;
        this.length = length;
    }
    
    private ByteBuffer getSlice() {
        return target.slice(offset, length);
    }
    
    @Override
    public boolean equals(Object other0) {
        if (!(other0 instanceof Address other))
            return false;
        return this.getSlice().equals(other.getSlice());
    }
    
    public void copyFrom(Address other) {
        target.mark();
        target.position(offset);
        target.put(other.getSlice());
        target.reset();
    }

    public Address newCopy() {
        try {
            Address copy = getClass().getConstructor(int.class).newInstance(length);
            copy.copyFrom(this);
            return copy;
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Support for ADR(record.field), where record is this {@link ManagedRecord}
     */
    public Address adr(int from, int size) {
        return new Address(target, offset + from, size);
    }
    
    /**
     * Support for INC(VAR a: ADDRESS, amount: CARDINAL)
     */
    public Address inc(int amount) {
        return new Address(target, offset + amount, length);
    }

    /**
     * Support for DEC(VAR a: ADDRESS, amount: CARDINAL)
     */
    public Address dec(int amount) {
        return new Address(target, offset - amount, length);
    }
    
    public void dispose() {
        target = null;
    }

}
