/* Test set arithmetic operators */
/* Union: set3 should contain {1, 3, 5, 7} */
/* Difference: set3 should contain {1} */
/* Intersection: set3 should contain {3, 5} */
/* Symmetric difference: set3 should contain {1, 7} */
package generated.test.set;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class SetAccess {

    // TYPE

    private static final Runtime.Range _ZeSet_r = new Runtime.Range(0, 31);

    private static final Runtime.Range _Set1_Set2_Set3_r = new Runtime.Range(0, 31);


    // VAR

    private Runtime.RangeSet zeSet = new Runtime.RangeSet(_ZeSet_r);
    private Runtime.RangeSet set1 = new Runtime.RangeSet(_Set1_Set2_Set3_r);
    private Runtime.RangeSet set2 = new Runtime.RangeSet(_Set1_Set2_Set3_r);
    private Runtime.RangeSet set3 = new Runtime.RangeSet(_Set1_Set2_Set3_r);
    private int index;
    private boolean test;


    public Runtime.RangeSet getZeSet() {
        return this.zeSet;
    }

    public void setZeSet(Runtime.RangeSet zeSet) {
        this.zeSet = zeSet;
    }

    public Runtime.RangeSet getSet1() {
        return this.set1;
    }

    public void setSet1(Runtime.RangeSet set1) {
        this.set1 = set1;
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
        set1.incl(1);
        set1.incl(3);
        set1.incl(5);
        set2.incl(3);
        set2.incl(5);
        set2.incl(7);
        set3 = Runtime.RangeSet.plus(set1, set2);
        set3 = Runtime.RangeSet.minus(set1, set2);
        set3 = Runtime.RangeSet.mul(set1, set2);
        set3 = Runtime.RangeSet.div(set1, set2);
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        SetAccess instance = new SetAccess();
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
