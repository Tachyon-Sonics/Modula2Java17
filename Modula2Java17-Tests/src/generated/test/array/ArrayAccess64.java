package generated.test.array;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ArrayAccess64 {

    // VAR

    private int[] array = new int[10];
    private long x;
    private long y;


    public int[] getArray() {
        return this.array;
    }

    public void setArray(int[] array) {
        this.array = array;
    }

    public long getX() {
        return this.x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return this.y;
    }

    public void setY(long y) {
        this.y = y;
    }


    // Life Cycle

    private void begin() {
        x = 4;
        y = 2;
        array[(int) (x + y)] = 42;
        x = array[(int) y];
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
