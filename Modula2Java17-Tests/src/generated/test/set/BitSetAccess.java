package generated.test.set;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class BitSetAccess {

    // VAR

    private Runtime.RangeSet zeSet = new Runtime.RangeSet(0, 15);
    private Runtime.RangeSet set2 = new Runtime.RangeSet(0, 15);
    private Runtime.RangeSet set3 = new Runtime.RangeSet(0, 15);
    private int index;
    private boolean test;


    public Runtime.RangeSet getZeSet() {
        return this.zeSet;
    }

    public void setZeSet(Runtime.RangeSet zeSet) {
        this.zeSet = zeSet;
    }

    public Runtime.RangeSet getSet2() {
        return this.set2;
    }

    public void setSet2(Runtime.RangeSet set2) {
        this.set2 = set2;
    }

    public Runtime.RangeSet getSet3() {
        return this.set3;
    }

    public void setSet3(Runtime.RangeSet set3) {
        this.set3 = set3;
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
        zeSet.incl(index);
        index = 6;
        zeSet.excl(index);
        index = 5;
        test = (zeSet.contains(index));
        zeSet = Runtime.RangeSet.plus(set2, set3);
        zeSet = Runtime.RangeSet.minus(set2, set3);
        zeSet = Runtime.RangeSet.mul(set2, set3);
        zeSet = Runtime.RangeSet.div(set2, set3);
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        BitSetAccess instance = new BitSetAccess();
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