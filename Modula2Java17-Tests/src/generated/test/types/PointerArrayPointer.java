package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class PointerArrayPointer {

    // TYPE

    @SuppressWarnings("unused")
    private static class CardPtr extends Runtime.Ref<Integer> {
    }



    // VAR

    private Runtime.IRef<Integer>[] buggy /* POINTER */;


    public Runtime.IRef<Integer>[] getBuggy() {
        return this.buggy;
    }

    public void setBuggy(Runtime.IRef<Integer>[] buggy) {
        this.buggy = buggy;
    }


    // Life Cycle

    @SuppressWarnings("unchecked")
    private void begin() {
        buggy = new Runtime.IRef[13];
        buggy[0] = new Runtime.Ref<>(0);
        buggy[0].set(42);
        buggy[0] = null;
        buggy = null;
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        PointerArrayPointer instance = new PointerArrayPointer();
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
