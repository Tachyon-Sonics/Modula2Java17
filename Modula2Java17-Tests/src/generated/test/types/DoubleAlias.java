package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DoubleAlias {

    // TYPE

    @SuppressWarnings("unused")
    private static class IntPtr extends Runtime.Ref<Short> {
    }



    // PROCEDURE

    @SuppressWarnings("unused")
    private void Dummy(Runtime.IRef<Short> arg) {
    }


    // Life Cycle

    private void begin() {
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        DoubleAlias instance = new DoubleAlias();
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
