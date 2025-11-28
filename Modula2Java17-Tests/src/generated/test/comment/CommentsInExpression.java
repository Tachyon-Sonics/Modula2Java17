package generated.test.comment;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class CommentsInExpression {

    // VAR

    private int x;
    private int y;
    private int z;
    private int result;


    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return this.z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
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
        result = x + y * z;
        /* Comment in a parenthesized expression */
        /* inside parentheses */
        result = (x + y) * z;
        /* Comment in a function-like call structure */
        /* between operators */
        result = x + (y * z);
        /* Multiple comments in one expression */
        /* start */
        /* middle */
        /* end */
        result = x + y - z;
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