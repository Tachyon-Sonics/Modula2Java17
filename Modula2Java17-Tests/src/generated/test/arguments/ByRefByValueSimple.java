package generated.test.arguments;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ByRefByValueSimple {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private short value;


    public short getValue() {
        return this.value;
    }

    public void setValue(short value) {
        this.value = value;
    }


    // PROCEDURE

    private void ModifyByRef(/* VAR */ Runtime.IRef<Short> value) {
        value.set((short) 42);
    }

    private void ModifyByValue(short value) {
        value = 42;
    }

    private void ModifyByRefByRef(/* VAR+WRT */ Runtime.IRef<Short> value) {
        ModifyByRef(value);
    }

    private void ModifyByRefByValue(/* var */ Runtime.IRef<Short> value) {
        ModifyByValue(value.get());
    }

    private void ModifyByValueByRef(/* WRT */ short _value) {
        Runtime.Ref<Short> value = new Runtime.Ref<>(_value);

        ModifyByRef(value);
    }

    private void ModifyByValueByValue(short value) {
        ModifyByValue(value);
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        value = 10;
        ModifyByValueByValue(value);
        ModifyByRefByValue(new Runtime.FieldRef<>(this::getValue, this::setValue));
        ModifyByValueByRef(value);
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
        ModifyByRefByRef(new Runtime.FieldRef<>(this::getValue, this::setValue));
        inOut.WriteInt(value, 2);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        ByRefByValueSimple instance = new ByRefByValueSimple();
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