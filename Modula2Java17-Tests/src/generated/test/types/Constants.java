package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Constants {

    // CONST

    private static final int Small = 42;
    private static final int Medium = 16777216;


    // VAR

    private int x;


    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }


    // Life Cycle

    private void begin() {
        x = Small;
        x = Medium;
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Constants instance = new Constants();
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
