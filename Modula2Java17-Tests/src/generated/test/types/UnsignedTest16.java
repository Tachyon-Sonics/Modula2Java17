package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class UnsignedTest16 {

    // VAR

    private int sc1;
    private int sc2;
    private int sc3;
    private int c1;
    private int c2;
    private int c3;
    private long lc1;
    private long lc2;
    private long lc3;
    private boolean t;


    public int getSc1() {
        return this.sc1;
    }

    public void setSc1(int sc1) {
        this.sc1 = sc1;
    }

    public int getSc2() {
        return this.sc2;
    }

    public void setSc2(int sc2) {
        this.sc2 = sc2;
    }

    public int getSc3() {
        return this.sc3;
    }

    public void setSc3(int sc3) {
        this.sc3 = sc3;
    }

    public int getC1() {
        return this.c1;
    }

    public void setC1(int c1) {
        this.c1 = c1;
    }

    public int getC2() {
        return this.c2;
    }

    public void setC2(int c2) {
        this.c2 = c2;
    }

    public int getC3() {
        return this.c3;
    }

    public void setC3(int c3) {
        this.c3 = c3;
    }

    public long getLc1() {
        return this.lc1;
    }

    public void setLc1(long lc1) {
        this.lc1 = lc1;
    }

    public long getLc2() {
        return this.lc2;
    }

    public void setLc2(long lc2) {
        this.lc2 = lc2;
    }

    public long getLc3() {
        return this.lc3;
    }

    public void setLc3(long lc3) {
        this.lc3 = lc3;
    }

    public boolean isT() {
        return this.t;
    }

    public void setT(boolean t) {
        this.t = t;
    }


    // Life Cycle

    private void begin() {
        sc1 = 20;
        sc2 = 6;
        sc3 = sc1 + sc2;
        sc3 = sc1 - sc2;
        sc3 = sc1 * sc2;
        sc3 = sc1 / sc2;
        sc3 = sc1 % sc2;
        t = (sc1 < sc2);
        t = (sc1 <= sc2);
        t = (sc1 > sc2);
        t = (sc1 >= sc2);
        t = (sc1 == sc2);
        c1 = 20;
        c2 = 6;
        c3 = c1 + c2;
        c3 = c1 - c2;
        c3 = c1 * c2;
        c3 = c1 / c2;
        c3 = c1 % c2;
        t = (c1 < c2);
        t = (c1 <= c2);
        t = (c1 > c2);
        t = (c1 >= c2);
        t = (c1 == c2);
        lc1 = 20;
        lc2 = 6;
        lc3 = lc1 + lc2;
        lc3 = lc1 - lc2;
        lc3 = lc1 * lc2;
        lc3 = lc1 / lc2;
        lc3 = lc1 % lc2;
        t = (lc1 < lc2);
        t = (lc1 <= lc2);
        t = (lc1 > lc2);
        t = (lc1 >= lc2);
        t = (lc1 == lc2);
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        UnsignedTest16 instance = new UnsignedTest16();
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