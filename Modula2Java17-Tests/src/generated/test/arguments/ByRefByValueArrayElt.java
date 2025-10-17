package generated.test.arguments;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ByRefByValueArrayElt {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private int[] value = new int[10];


    public int[] getValue() {
        return this.value;
    }

    public void setValue(int[] value) {
        this.value = value;
    }


    // PROCEDURE

    private void ModifyByRef(/* VAR */ int[] value) {
        value[0] = 42;
    }

    private void ModifyByValue(int[] _value) {
        int[] value = Runtime.copyOf(true, _value); // By-value and written argument

        value[0] = 42;
    }

    private void ModifyByRefByRef(/* VAR+WRT */ int[] value) {
        ModifyByRef(value);
    }

    private void ModifyByRefByValue(/* var */ int[] value) {
        ModifyByValue(value);
    }

    private void ModifyByValueByRef(/* WRT */ int[] _value) {
        int[] value = Runtime.copyOf(true, _value); // By-value and written argument

        ModifyByRef(value);
    }

    private void ModifyByValueByValue(int[] value) {
        ModifyByValue(value);
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        value[0] = 10;
        ModifyByValueByValue(value);
        ModifyByRefByValue(value);
        ModifyByValueByRef(value);
        inOut.WriteInt(value[0], 2);
        inOut.WriteLn();
        ModifyByRefByRef(value);
        inOut.WriteInt(value[0], 2);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        ByRefByValueArrayElt instance = new ByRefByValueArrayElt();
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