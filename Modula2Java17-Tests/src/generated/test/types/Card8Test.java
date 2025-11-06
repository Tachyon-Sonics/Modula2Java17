package generated.test.types;

import ch.pitchtech.modula.library.iso.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class Card8Test {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private byte sc1;
    private byte sc2;
    private byte sc3;


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


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        sc1 = (byte) 255;
        sc2 = 17;
        sc3 = (byte) (Byte.toUnsignedInt(sc1) / Byte.toUnsignedInt(sc2));
        inOut.WriteCard((short) Byte.toUnsignedInt(sc3), (short) 2);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        Card8Test instance = new Card8Test();
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