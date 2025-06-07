package generated.test;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Basic {

    // Life Cycle

    private void begin() {
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Basic instance = new Basic();
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
