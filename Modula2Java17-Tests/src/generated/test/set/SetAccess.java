package generated.test.set;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class SetAccess {

    // VAR

    private Runtime.RangeSet zeSet = new Runtime.RangeSet(0, 31);
    private int index;
    private boolean test;


    public Runtime.RangeSet getZeSet() {
        return this.zeSet;
    }

    public void setZeSet(Runtime.RangeSet zeSet) {
        this.zeSet = zeSet;
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
