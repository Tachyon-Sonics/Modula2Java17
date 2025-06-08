package generated.test;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Primes {

    // Imports
    private final InOut inOut = InOut.instance();


    // CONST

    private static final int N = 500;
    private static final int M = 23;
    private static final int LL = 10;


    // VAR

    private short i;
    private short k;
    private short x;
    private short inc;
    private short lim;
    private short square;
    private short L;
    private boolean prime;
    private short[] P = new short[M + 1];
    private short[] V = new short[M + 1];


    public short getI() {
        return this.i;
    }

    public void setI(short i) {
        this.i = i;
    }

    public short getK() {
        return this.k;
    }

    public void setK(short k) {
        this.k = k;
    }

    public short getX() {
        return this.x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public short getInc() {
        return this.inc;
    }

    public void setInc(short inc) {
        this.inc = inc;
    }

    public short getLim() {
        return this.lim;
    }

    public void setLim(short lim) {
        this.lim = lim;
    }

    public short getSquare() {
        return this.square;
    }

    public void setSquare(short square) {
        this.square = square;
    }

    public short getL() {
        return this.L;
    }

    public void setL(short L) {
        this.L = L;
    }

    public boolean isPrime() {
        return this.prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }

    public short[] getP() {
        return this.P;
    }

    public void setP(short[] P) {
        this.P = P;
    }

    public short[] getV() {
        return this.V;
    }

    public void setV(short[] V) {
        this.V = V;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        L = 0;
        x = 1;
        inc = 4;
        lim = 1;
        square = 9;
        for (i = 3; i <= N; i++) {
            do {
                x = (short) (x + inc);
                inc = (short) (6 - inc);
                if (square <= x) {
                    lim++;
                    V[lim] = square;
                    square = (short) (P[lim + 1] * P[lim + 1]);
                }
                k = 2;
                prime = true;
                while (prime && (k < lim)) {
                    k++;
                    if (V[k] < x)
                        V[k] = (short) (V[k] + 2 * P[k]);
                    prime = x != V[k];
                }
            } while (!prime);
            if (i < M)
                P[i] = x;
            inOut.WriteInt(x, 6);
            L++;
            if (L == LL) {
                inOut.WriteLn();
                L = 0;
            }
        }
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Primes instance = new Primes();
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