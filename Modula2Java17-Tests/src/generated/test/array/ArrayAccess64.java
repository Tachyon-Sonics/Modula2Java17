package generated.test.array;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ArrayAccess64 {

    // VAR

    private int[] array = new int[10];
    private long index;


    public int[] getArray() {
        return this.array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public long getIndex() {
        return this.index;
    }

    public void setIndex(long index) {
        this.index = index;
    }


    // Life Cycle

    private void begin() {
        index = 4;
        array[(int) index] = 42;
        index = array[4];
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        ArrayAccess64 instance = new ArrayAccess64();
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