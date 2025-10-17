package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DataModelStrict16 {

    // VAR

    private byte si1;
    private byte si2;
    private byte sc1;
    private byte sc2;
    private short i1;
    private short i2;
    private short c1;
    private short c2;
    private int li1;
    private int li2;
    private int lc1;
    private int lc2;


    public byte getSi1() {
        return this.si1;
    }

    public void setSi1(byte si1) {
        this.si1 = si1;
    }

    public byte getSi2() {
        return this.si2;
    }

    public void setSi2(byte si2) {
        this.si2 = si2;
    }

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

    public short getI1() {
        return this.i1;
    }

    public void setI1(short i1) {
        this.i1 = i1;
    }

    public short getI2() {
        return this.i2;
    }

    public void setI2(short i2) {
        this.i2 = i2;
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

    public int getLi1() {
        return this.li1;
    }

    public void setLi1(int li1) {
        this.li1 = li1;
    }

    public int getLi2() {
        return this.li2;
    }

    public void setLi2(int li2) {
        this.li2 = li2;
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


    // Life Cycle

    private void begin() {
        si1 = 10;
        si2 = -4;
        si1 = (byte) (si1 * si2);
        sc1 = 7;
        sc2 = 9;
        sc1 = (byte) (sc1 * sc2);
        i1 = 10;
        i2 = -4;
        i1 = (short) (i1 * i2);
        c1 = 7;
        c2 = 9;
        c1 = (short) (c1 * c2);
        li1 = 10;
        li2 = -4;
        li1 = li1 * li2;
        lc1 = 7;
        lc2 = 9;
        lc1 = lc1 * lc2;
    }

    private void close() {
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        DataModelStrict16 instance = new DataModelStrict16();
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