package generated.test;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class PowersOf2 {

    // Imports
    private final InOut inOut = InOut.instance();


    // CONST

    private static final int M = 11;
    private static final int N = 32;


    // VAR

    private short i;
    private short j;
    private short k;
    private short exp;
    private short carry;
    private short rest;
    private short t;
    private short[] d = new short[M + 1];
    private short[] f = new short[N + 1];


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

    public short getK() {
        return this.k;
    }

    public void setK(short k) {
        this.k = k;
    }

    public short getExp() {
        return this.exp;
    }

    public void setExp(short exp) {
        this.exp = exp;
    }

    public short getCarry() {
        return this.carry;
    }

    public void setCarry(short carry) {
        this.carry = carry;
    }

    public short getRest() {
        return this.rest;
    }

    public void setRest(short rest) {
        this.rest = rest;
    }

    public short getT() {
        return this.t;
    }

    public void setT(short t) {
        this.t = t;
    }

    public short[] getD() {
        return this.d;
    }

    public void setD(short[] d) {
        this.d = d;
    }

    public short[] getF() {
        return this.f;
    }

    public void setF(short[] f) {
        this.f = f;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        d[0] = 1;
        k = 1;
        for (exp = 1; exp <= N; exp++) {
            carry = 0;
            for (i = 0; i < k; i++) {
                t = (short) (2 * d[i] + carry);
                if (t >= 10) {
                    d[i] = (short) (t - 10);
                    carry = 1;
                } else {
                    d[i] = t;
                    carry = 0;
                }
            }
            if (carry > 0) {
                d[k] = 1;
                k++;
            }
            i = M;
            do {
                i--;
                inOut.Write(' ');
            } while (i != k);
            do {
                i--;
                inOut.Write((char) (d[i] + '0'));
            } while (i != 0);
            inOut.WriteInt(exp, 5);
            inOut.WriteString("  0.");
            rest = 0;
            for (j = 1; j < exp; j++) {
                rest = (short) (10 * rest + f[j]);
                f[j] = (short) (rest / 2);
                rest = (short) (rest % 2);
                inOut.Write((char) (f[j] + '0'));
            }
            f[exp] = 5;
            inOut.Write('5');
            inOut.WriteLn();
            inOut.WriteBf();
        }
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        PowersOf2 instance = new PowersOf2();
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
