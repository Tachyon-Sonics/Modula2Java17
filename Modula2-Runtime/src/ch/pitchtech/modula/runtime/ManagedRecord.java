package ch.pitchtech.modula.runtime;

import ch.pitchtech.modula.runtime.Runtime.RangeSet;

public abstract class ManagedRecord<T extends ManagedRecord<T>> extends Address {
    
    /**
     * Used when created by {@link StorageImpl#allocate(int)} or NEW()
     */
    public ManagedRecord(int size) {
        super(size);
    }
    
    /**
     * Used when type-casting from an ADDRESS/POINTER
     */
    public ManagedRecord(Address target) {
        super(target);
    }
    
    // Get/Set primitive types
    
    public void setS8(int offset, byte value) {
        target.put(super.offset + offset, value);
    }
    
    public byte getS8(int offset) {
        return target.get(super.offset + offset);
    }
    
    public void setU8(int offset, short value) {
        target.put(super.offset + offset, (byte) value);
    }
    
    public short getU8(int offset) {
        byte result = target.get(super.offset + offset);
        return (short) ((int) result & 0xFF);
    }
    
    public void setBool(int offset, boolean value) {
        setS8(offset, value ? (byte) -1 : (byte) 0);
    }
    
    public boolean getBool(int offset) {
        byte value = getS8(offset);
        return (value != 0);
    }
    
    public void setS16(int offset, short value) {
        target.putShort(super.offset + offset, value);
    }
    
    public short getS16(int offset) {
        return target.getShort(super.offset + offset);
    }
    
    public void setU16(int offset, int value) {
        target.putShort(super.offset + offset, (short) value);
    }
    
    public int getU16(int offset) {
        short result = target.getShort(super.offset + offset);
        return (int) result & 0xFFFF;
    }
    
    public void setEnum(int offset, Enum<?> value) {
        setU16(offset, value.ordinal());
    }
    
    public <E extends Enum<E>> E getEnum(int offset, Class<E> enumType) {
        int ordinal = getU16(offset);
        E result = enumType.getEnumConstants()[ordinal];
        return result;
    }
    
    public void setRS(int offset, RangeSet value) {
        setS16(offset, value.toWord());
    }
    
    public void getRS(int offset, RangeSet target) {
        short word = getS16(offset);
        target.fromWord(word);
    }
    
    public void setS32(int offset, int value) {
        target.putInt(super.offset + offset, value);
    }
    
    public int getS32(int offset) {
        return target.getInt(super.offset + offset);
    }
    
    public void setU32(int offset, long value) {
        target.putInt(super.offset + offset, (int) value);
    }
    
    public long getU32(int offset) {
        int result = target.getInt(super.offset + offset);
        return (long) result & 0xFFFFFFFFL;
    }
    
    public void setR32(int offset, float value) {
        target.putFloat(super.offset + offset, value);
    }
    
    public float getR32(int offset) {
        return target.getFloat(super.offset + offset);
    }
    
    public void setS64(int offset, long value) {
        target.putLong(super.offset + offset, value);
    }
    
    public long getS64(int offset) {
        return target.getLong(super.offset + offset);
    }
    
    public void setU64(int offset, long value) {
        target.putLong(super.offset + offset, value);
    }
    
    public long getU64(int offset) {
        return target.getLong(super.offset + offset);
    }
    
    public void setR64(int offset, double value) {
        target.putDouble(super.offset + offset, value);
    }
    
    public double getR64(int offset) {
        return target.getDouble(super.offset + offset);
    }
    
}
