package generated.test;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Harmonic {

    // Imports
    private final InOut inOut = InOut.instance();
    private final RealInOut realInOut = RealInOut.instance();


    // VAR

    private int i;
    private int n;
    private double x;
    private double d;
    private double s1;
    private double s2;


    public int getI() {
        return this.i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getN() {
        return this.n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getD() {
        return this.d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getS1() {
        return this.s1;
    }

    public void setS1(double s1) {
        this.s1 = s1;
    }

    public double getS2() {
        return this.s2;
    }

    public void setS2(double s2) {
        this.s2 = s2;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();
        RealInOut.instance().begin();

        inOut.WriteString("n = ");
        inOut.WriteBf();
        inOut.ReadLongInt(new Runtime.FieldRef<>(this::getN, this::setN));
        while (inOut.Done) {
            s1 = 0.0;
            d = 0.0;
            i = 0;
            do {
                d = d + 1.0;
                s1 = s1 + 1.0 / d;
                i++;
            } while (i < n);
            realInOut.WriteLongReal(s1, 16);
            inOut.Write(((char) 011));
            s2 = 0.0;
            do {
                s2 = s2 + 1.0 / d;
                d = d - 1.0;
                i--;
            } while (i != 0);
            realInOut.WriteLongReal(s2, 16);
            inOut.Write(((char) 011));
            inOut.WriteString("  Diff = ");
            realInOut.WriteLongReal(100.0 * (s2 - s1) / s1, 16);
            inOut.Write('%');
            inOut.WriteLn();
            inOut.WriteString("n = ");
            inOut.WriteBf();
            inOut.ReadLongInt(new Runtime.FieldRef<>(this::getN, this::setN));
        }
        inOut.WriteLn();
    }

    private void close() {
        RealInOut.instance().close();
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Harmonic instance = new Harmonic();
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
