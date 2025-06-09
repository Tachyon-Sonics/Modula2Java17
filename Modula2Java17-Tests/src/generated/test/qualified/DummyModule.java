package generated.test.qualified;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DummyModule {

    // Imports
    private final DummyLibrary dummyLibrary = DummyLibrary.instance();


    // Life Cycle

    private void begin() {
        DummyLibrary.instance().begin();

        dummyLibrary.DummyProcedure(42);
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