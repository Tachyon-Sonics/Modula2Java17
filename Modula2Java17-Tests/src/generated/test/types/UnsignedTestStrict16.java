package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class UnsignedTestStrict16 {

    // VAR

    private byte sc1;
    private byte sc2;
    private byte sc3;
    private short c1;
    private short c2;
    private short c3;
    private int lc1;
    private int lc2;
    private int lc3;
    private boolean t;


    public byte getSc1() {
        return this.sc1;
    }

    public void setSc1(byte sc1) {
        this.sc1 = sc1;
    }

    public byte getSc2() {
        return this.sc2;
    }

    public void setSc2(byte sc2) {
        this.sc2 = sc2;
    }

    public byte getSc3() {
        return this.sc3;
    }

    public void setSc3(byte sc3) {
        this.sc3 = sc3;
    }

    public short getC1() {
        return this.c1;
    }

    public void setC1(short c1) {
        this.c1 = c1;
    }

    public short getC2() {
        return this.c2;
    }

    public void setC2(short c2) {
        this.c2 = c2;
    }

    public short getC3() {
        return this.c3;
    }

    public void setC3(short c3) {
        this.c3 = c3;
    }

    public int getLc1() {
        return this.lc1;
    }

    public void setLc1(int lc1) {
        this.lc1 = lc1;
    }

    public int getLc2() {
        return this.lc2;
    }

    public void setLc2(int lc2) {
        this.lc2 = lc2;
    }

    public int getLc3() {
        return this.lc3;
    }

    public void setLc3(int lc3) {
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
        sc3 = (byte) (sc1 + sc2);
        sc3 = (byte) (sc1 - sc2);
        sc3 = (byte) (sc1 * sc2);
        sc3 = (byte) (Integer.divideUnsigned(sc1, sc2));
        sc3 = (byte) (Integer.remainderUnsigned(sc1, sc2));
        t = (Byte.compareUnsigned(sc1, sc2) < 0);
        t = (Byte.compareUnsigned(sc1, sc2) <= 0);
        t = (Byte.compareUnsigned(sc1, sc2) > 0);
        t = (Byte.compareUnsigned(sc1, sc2) >= 0);
        t = (sc1 == sc2);
        c1 = 20;
        c2 = 6;
        c3 = (short) (c1 + c2);
        c3 = (short) (c1 - c2);
        c3 = (short) (c1 * c2);
        c3 = (short) (Integer.divideUnsigned(c1, c2));
        c3 = (short) (Integer.remainderUnsigned(c1, c2));
        t = (Short.compareUnsigned(c1, c2) < 0);
        t = (Short.compareUnsigned(c1, c2) <= 0);
        t = (Short.compareUnsigned(c1, c2) > 0);
        t = (Short.compareUnsigned(c1, c2) >= 0);
        t = (c1 == c2);
        lc1 = 20;
        lc2 = 6;
        lc3 = lc1 + lc2;
        lc3 = lc1 - lc2;
        lc3 = lc1 * lc2;
        lc3 = Integer.divideUnsigned(lc1, lc2);
        lc3 = Integer.remainderUnsigned(lc1, lc2);
        t = (Integer.compareUnsigned(lc1, lc2) < 0);
        t = (Integer.compareUnsigned(lc1, lc2) <= 0);
        t = (Integer.compareUnsigned(lc1, lc2) > 0);
        t = (Integer.compareUnsigned(lc1, lc2) >= 0);
        t = (lc1 == lc2);
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        UnsignedTestStrict16 instance = new UnsignedTestStrict16();
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