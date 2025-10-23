package generated.test.set;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class BitsetTest {

    // VAR

    private Runtime.RangeSet bits = new Runtime.RangeSet(0, 15);
    private int index;
    private boolean test;


    public Runtime.RangeSet getBits() {
        return this.bits;
    }

    public void setBits(Runtime.RangeSet bits) {
        this.bits = bits;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isTest() {
        return this.test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }


    // Life Cycle

    private void begin() {
        index = 4;
        bits.incl(index);
        index = 6;
        bits.excl(index);
        index = 5;
        test = (bits.contains(index));
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        BitsetTest instance = new BitsetTest();
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
