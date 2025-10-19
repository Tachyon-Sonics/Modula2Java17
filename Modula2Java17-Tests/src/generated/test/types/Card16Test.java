package generated.test.types;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Card16Test {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private short sc1;
    private short sc2;
    private short sc3;


    public short getSc1() {
        return this.sc1;
    }

    public void setSc1(short sc1) {
        this.sc1 = sc1;
    }

    public short getSc2() {
        return this.sc2;
    }

    public void setSc2(short sc2) {
        this.sc2 = sc2;
    }

    public short getSc3() {
        return this.sc3;
    }

    public void setSc3(short sc3) {
        this.sc3 = sc3;
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        sc1 = (short) 65535;
        sc2 = 255;
        sc3 = (short) (Short.toUnsignedInt(sc1) / Short.toUnsignedInt(sc2));
        inOut.WriteCard(sc3, (short) 3);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Card16Test instance = new Card16Test();
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