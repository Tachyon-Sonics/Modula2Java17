package generated.test.types;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class UnsignedCast32 {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private int[] arr = new int[256];
    private short index;
    private int index2;
    private long index3;
    private int value;
    private long big;


    public int[] getArr() {
        return this.arr;
    }

    public void setArr(int[] arr) {
        this.arr = arr;
    }

    public short getIndex() {
        return this.index;
    }

    public void setIndex(short index) {
        this.index = index;
    }

    public int getIndex2() {
        return this.index2;
    }

    public void setIndex2(int index2) {
        this.index2 = index2;
    }

    public long getIndex3() {
        return this.index3;
    }

    public void setIndex3(long index3) {
        this.index3 = index3;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public long getBig() {
        return this.big;
    }

    public void setBig(long big) {
        this.big = big;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        arr[255] = 42;
        index = 255;
        /* SHORTCARD array access */
        value = arr[Short.toUnsignedInt(index)];
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
        /* Upcast to CARDINAL and LONGCARD */
        index2 = Short.toUnsignedInt(index);
        index3 = Short.toUnsignedLong(index);
        index3 = Integer.toUnsignedLong(index2);
        /* CARDINAL array access */
        value = arr[index2];
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
        /* Upcast to INTEGER */
        value = Short.toUnsignedInt(index);
        value = index2;
        value = (int) index3;
        /* Upcast to LONGINT */
        big = Short.toUnsignedLong(index);
        big = Integer.toUnsignedLong(index2);
        big = index3;
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        UnsignedCast32 instance = new UnsignedCast32();
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