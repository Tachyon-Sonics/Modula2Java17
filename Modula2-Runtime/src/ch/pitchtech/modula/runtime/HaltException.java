package ch.pitchtech.modula.runtime;


/**
 * Exception raised by a Modula-2 "HALT" instruction.
 * <p>
 * This is used in place of <tt>System.exit()</tt> so we can implement "CLOSE" statements
 * using a Java "finally" block
 */
public class HaltException extends RuntimeException {

    public HaltException() {
        super("\"HALT\" instruction reached");
    }

}
