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

    private int i;
    private int k;
    private int x;
    private int inc;
    private int lim;
    private int square;
    private int L;
    private boolean prime;
    private int[] P = new int[M + 1];
    private int[] V = new int[M + 1];


    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getK() {
        return this.k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getInc() {
        return this.inc;
    }

    public void setInc(int inc) {
        this.inc = inc;
    }

    public int getLim() {
        return this.lim;
    }

    public void setLim(int lim) {
        this.lim = lim;
    }

    public int getSquare() {
        return this.square;
    }

    public void setSquare(int square) {
        this.square = square;
    }

    public int getL() {
        return this.L;
    }

    public void setL(int L) {
        this.L = L;
    }

    public boolean isPrime() {
        return this.prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }

    public int[] getP() {
        return this.P;
    }

    public void setP(int[] P) {
        this.P = P;
    }

    public int[] getV() {
        return this.V;
    }

    public void setV(int[] V) {
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
                x = x + inc;
                inc = 6 - inc;
                if (square <= x) {
                    lim++;
                    V[lim] = square;
                    square = P[lim + 1] * P[lim + 1];
                }
                k = 2;
                prime = true;
                while (prime && (k < lim)) {
                    k++;
                    if (V[k] < x)
                        V[k] = V[k] + 2 * P[k];
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