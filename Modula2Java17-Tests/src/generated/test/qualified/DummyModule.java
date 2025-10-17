package generated.test.qualified;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DummyModule {

    // Imports
    private final DummyLibrary dummyLibrary = DummyLibrary.instance();


    // VAR

    private long result;
    private int temp;
    private DummyLibrary.DummyType test = new DummyLibrary.DummyType();


    public long getResult() {
        return this.result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    public int getTemp() {
        return this.temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public DummyLibrary.DummyType getTest() {
        return this.test;
    }

    public void setTest(DummyLibrary.DummyType test) {
        this.test = test;
    }


    // PROCEDURE

    private DummyLibrary.DummyType TestProc(DummyLibrary.DummyType arg) {
        return arg;
    }


    // Life Cycle

    private void begin() {
        DummyLibrary.instance().begin();

        dummyLibrary.DummyProcedure(42);
        result = dummyLibrary.DummyFunction(42);
        result = DummyLibrary.DummyConst;
        temp = dummyLibrary.DummyVariable;
        dummyLibrary.DummyVariable = temp;
        test.x = 10;
        temp = test.x;
        temp = dummyLibrary.DummyTypeVariable.x;
        dummyLibrary.DummyTypeVariable.y = temp;
        dummyLibrary.DummyTypeVariable.copyFrom(test);
        test.copyFrom(dummyLibrary.DummyTypeVariable);
        test.copyFrom(TestProc(test));
        test.copyFrom(dummyLibrary.TestFunction(test));
    }

    private void close() {
        DummyLibrary.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        DummyModule instance = new DummyModule();
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