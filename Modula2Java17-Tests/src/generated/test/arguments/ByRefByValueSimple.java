package generated.test.arguments;

import ch.pitchtech.modula.library.iso.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class ByRefByValueSimple {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private int value;


    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }


    // PROCEDURE

    private void ModifyByRef(/* VAR */ Runtime.IRef<Integer> value) {
        value.set(42);
    }

    private void ModifyByValue(int value) {
        value = 42;
    }

    private void ModifyByRefByRef(/* VAR+WRT */ Runtime.IRef<Integer> value) {
        ModifyByRef(value);
    }

    private void ModifyByRefByValue(/* var */ Runtime.IRef<Integer> value) {
        ModifyByValue(value.get());
    }

    private void ModifyByValueByRef(/* WRT */ int _value) {
        Runtime.Ref<Integer> value = new Runtime.Ref<>(_value);

        ModifyByRef(value);
    }

    private void ModifyByValueByValue(int value) {
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