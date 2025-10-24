package generated.test.types;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class UnsignedCast {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private short[] arr = new short[256];
    private byte index;
    private short index2;
    private int index3;
    private short value;
    private int big;


    public short[] getArr() {
        return this.arr;
    }

    public void setArr(short[] arr) {
        this.arr = arr;
    }

    public byte getIndex() {
        return this.index;
    }

    public void setIndex(byte index) {
        this.index = index;
    }

    public short getIndex2() {
        return this.index2;
    }

    public void setIndex2(short index2) {
        this.index2 = index2;
    }

    public int getIndex3() {
        return this.index3;
    }

    public void setIndex3(int index3) {
        this.index3 = index3;
    }

    public short getValue() {
        return this.value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    public int getBig() {
        return this.big;
    }

    public void setBig(int big) {
        this.big = big;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        arr[255] = 42;
        index = (byte) 255;
        /* SHORTCARD array access */
        value = arr[Byte.toUnsignedInt(index)];
        inOut.WriteInt(value, (short) 2);
        inOut.WriteLn();
        /* Upcast to CARDINAL and LONGCARD */
        index2 = (short) Byte.toUnsignedInt(index);
        index3 = Byte.toUnsignedInt(index);
        index3 = Short.toUnsignedInt(index2);
        /* CARDINAL array access */
        value = arr[Short.toUnsignedInt(index2)];
        inOut.WriteInt(value, (short) 2);
        inOut.WriteLn();
        /* Upcast to INTEGER */
        value = (short) Byte.toUnsignedInt(index);
        value = index2;
        value = (short) index3;
        /* Upcast to LONGINT */
        big = Byte.toUnsignedInt(index);
        big = Short.toUnsignedInt(index2);
        big = index3;
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        UnsignedCast instance = new UnsignedCast();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}