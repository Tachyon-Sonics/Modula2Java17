package generated.test;

import ch.pitchtech.modula.library.iso.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Fractions32 {

    // Imports
    private final InOut inOut = InOut.instance();


    // CONST

    private static final int Base = 10;
    private static final int N = 32;


    // VAR

    private int i;
    private int j;
    private int m;
    private int rem;
    private int[] d = new int[N];
    private int[] x = new int[N + 1];


    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return this.j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getM() {
        return this.m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public int getRem() {
        return this.rem;
    }

    public void setRem(int rem) {
        this.rem = rem;
    }

    public int[] getD() {
        return this.d;
    }

    public void setD(int[] d) {
        this.d = d;
    }

    public int[] getX() {
        return this.x;
    }

    public void setX(int[] x) {
        this.x = x;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        for (i = 2; i <= N; i++) {
            for (j = 0; j < i; j++) {
                x[j] = 0;
            }
            m = 0;
            rem = 1;
            do {
                m++;
                x[rem] = m;
                rem = Base * rem;
                d[m - 1] = rem / i;
                rem = rem % i;
            } while (x[rem] == 0);
            inOut.WriteInt(i, 6);
            inOut.WriteString(" 0.");
            for (j = 1; j < x[rem]; j++) {
                inOut.Write((char) (d[j - 1] + '0'));
            }
            inOut.Write('\'');
            for (j = x[rem]; j <= m; j++) {
                inOut.Write((char) (d[j - 1] + '0'));
            }
            inOut.WriteLn();
        }
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Fractions32 instance = new Fractions32();
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
