package generated.test.types;

import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class DataModel16 {

    // VAR

    private int si1;
    private int si2;
    private int sc1;
    private int sc2;
    private int i1;
    private int i2;
    private int c1;
    private int c2;
    private long li1;
    private long li2;
    private long lc1;
    private long lc2;


    public int getSi1() {
        return this.si1;
    }

    public void setSi1(int si1) {
        this.si1 = si1;
    }

    public int getSi2() {
        return this.si2;
    }

    public void setSi2(int si2) {
        this.si2 = si2;
    }

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

    public int getI1() {
        return this.i1;
    }

    public void setI1(int i1) {
        this.i1 = i1;
    }

    public int getI2() {
        return this.i2;
    }

    public void setI2(int i2) {
        this.i2 = i2;
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

    public long getLi1() {
        return this.li1;
    }

    public void setLi1(long li1) {
        this.li1 = li1;
    }

    public long getLi2() {
        return this.li2;
    }

    public void setLi2(long li2) {
        this.li2 = li2;
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


    // Life Cycle

    private void begin() {
        si1 = 10;
        si2 = -4;
        si1 = si1 * si2;
        sc1 = 7;
        sc2 = 9;
        sc1 = sc1 * sc2;
        i1 = 10;
        i2 = -4;
        i1 = i1 * i2;
        c1 = 7;
        c2 = 9;
        c1 = c1 * c2;
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
        DataModel16 instance = new DataModel16();
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