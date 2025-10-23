package generated.test.test;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class CommentsInExpression {

    // VAR

    private short x;
    private short y;
    private short z;
    private short result;


    public short getX() {
        return this.x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getY() {
        return this.y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public short getZ() {
        return this.z;
    }

    public void setZ(short z) {
        this.z = z;
    }

    public short getResult() {
        return this.result;
    }

    public void setResult(short result) {
        this.result = result;
    }


    // Life Cycle

    private void begin() {
        x = 10;
        y = 20;
        z = 30;
        /* Comment before a complex expression */
        /* first operand */
        /* second operand */
        /* third operand */
        result = (short) (x + y * z);
        /* Comment in a parenthesized expression */
        /* inside parentheses */
        result = (short) ((x + y) * z);
        /* Comment in a function-like call structure */
        /* between operators */
        result = (short) (x + (y * z));
        /* Multiple comments in one expression */
        /* start */
        /* middle */
        /* end */
        result = (short) (x + y - z);
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        CommentsInExpression instance = new CommentsInExpression();
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
