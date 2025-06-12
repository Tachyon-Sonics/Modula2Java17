package generated.test.qualified;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DummyModule {

    // Imports
    private final DummyLibrary dummyLibrary = DummyLibrary.instance();


    // VAR

    private int result;
    private short temp;
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


    // Life Cycle

    private void begin() {
        DummyLibrary.instance().begin();

        dummyLibrary.DummyProcedure(42);
        result = dummyLibrary.DummyFunction(42);
        result = DummyLibrary.DummyConst;
        temp = dummyLibrary.DummyVariable;
        dummyLibrary.DummyVariable = temp;
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