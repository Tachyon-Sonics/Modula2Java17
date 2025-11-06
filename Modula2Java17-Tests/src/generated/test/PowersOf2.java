package generated.test;

import ch.pitchtech.modula.library.iso.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class PowersOf2 {

    // Imports
    private final InOut inOut = InOut.instance();


    // CONST

    private static final int M = 11;
    private static final int N = 32;


    // VAR

    private int i;
    private int j;
    private int k;
    private int exp;
    private int carry;
    private int rest;
    private int t;
    private int[] d = new int[M + 1];
    private int[] f = new int[N + 1];


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

    public int getK() {
        return this.k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getExp() {
        return this.exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getCarry() {
        return this.carry;
    }

    public void setCarry(int carry) {
        this.carry = carry;
    }

    public int getRest() {
        return this.rest;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getT() {
        return this.t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int[] getD() {
        return this.d;
    }

    public void setD(int[] d) {
        this.d = d;
    }

    public int[] getF() {
        return this.f;
    }

    public void setF(int[] f) {
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
                t = 2 * d[i] + carry;
                if (t >= 10) {
                    d[i] = t - 10;
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
                rest = 10 * rest + f[j];
                f[j] = rest / 2;
                rest = rest % 2;
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