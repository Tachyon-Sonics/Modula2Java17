package generated.test.comment;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class NestedComments {

    // VAR

    /* This is a simple comment */
    private int x;


    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }


    // Life Cycle

    private void begin() {
        /* This is a nested comment (* inner comment *) outer continues */
        x = 42;
        /* Multi-level nesting
               (* level 2
                  (* level 3 *)
               level 2 again *)
            level 1 again */
        x = x + 1;
        /* Comment with (* nested (* double nested *) *) still in comment */
        x = x * 2;
        /*
         * Multi-line
         * comment
         */
        x = x / 4;
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        NestedComments instance = new NestedComments();
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