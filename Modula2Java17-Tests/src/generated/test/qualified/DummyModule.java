package generated.test.qualified;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DummyModule {

    // Imports
    private final DummyLibrary dummyLibrary = DummyLibrary.instance();


    // VAR

    private int result;
    private short temp;
    /* Qualified type access */
    private DummyLibrary.DummyType test = new DummyLibrary.DummyType();


    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public short getTemp() {
        return this.temp;
    }

    public void setTemp(short temp) {
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

        /* Qualified procedure call */
        dummyLibrary.DummyProcedure(42);
        /* Qualified function call */
        result = dummyLibrary.DummyFunction(42);
        /* Qualified constant access */
        result = DummyLibrary.DummyConst;
        /* Qualified var accesses, read, write */
        temp = dummyLibrary.DummyVariable;
        dummyLibrary.DummyVariable = temp;
        /* Access to var of qualified type */
        test.x = 10;
        temp = test.x;
        /* Access to qualified var of qualified type */
        temp = dummyLibrary.DummyTypeVariable.x;
        dummyLibrary.DummyTypeVariable.y = temp;
        dummyLibrary.DummyTypeVariable.copyFrom(test);
        test.copyFrom(dummyLibrary.DummyTypeVariable);
        /* Qualified arg and result */
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
