package generated.test.set;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class SetAccess64 {

    // TYPE

    private static final Runtime.Range MySet_r = new Runtime.Range(0, 63);


    // VAR

    private Runtime.RangeSet set = new Runtime.RangeSet(MySet_r);
    private long index;
    private boolean test;


    public Runtime.RangeSet getSet() {
        return this.set;
    }

    public void setSet(Runtime.RangeSet set) {
        this.set = set;
    }

    public long getIndex() {
        return this.index;
    }

    public void setIndex(long index) {
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
        set.incl((int) index);
        index = 6;
        set.excl((int) index);
        index = 5;
        test = (set.contains((int) index));
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        SetAccess64 instance = new SetAccess64();
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