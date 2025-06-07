package generated.test;

import ch.pitchtech.modula.library.InOut;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Fractions {

    // Imports
    private final InOut inOut = InOut.instance();


    // CONST

    private static final int Base = 10;
    private static final int N = 32;


    // VAR

    private short i;
    private short j;
    private short m;
    private short rem;
    private short[] d = new short[N];
    private short[] x = new short[N + 1];


    public short getI() {
        return this.i;
    }

    public void setI(short i) {
        this.i = i;
    }

    public short getJ() {
        return this.j;
    }

    public void setJ(short j) {
        this.j = j;
    }

    public short getM() {
        return this.m;
    }

    public void setM(short m) {
        this.m = m;
    }

    public short getRem() {
        return this.rem;
    }

    public void setRem(short rem) {
        this.rem = rem;
    }

    public short[] getD() {
        return this.d;
    }

    public void setD(short[] d) {
        this.d = d;
    }

    public short[] getX() {
        return this.x;
    }

    public void setX(short[] x) {
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
                rem = (short) (Base * rem);
                d[m - 1] = (short) (rem / i);
                rem = (short) (rem % i);
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
        Fractions instance = new Fractions();
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
