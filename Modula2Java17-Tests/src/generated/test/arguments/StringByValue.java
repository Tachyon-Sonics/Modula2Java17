package generated.test.arguments;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class StringByValue {

    // Imports
    private final InOut inOut = InOut.instance();


    // VAR

    private String text = "";


    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }


    // PROCEDURE

    private void ModifyStringByRef(/* VAR */ Runtime.IRef<String> str) {
        str.set("Modified");
    }

    private void ModifyStringByValue(/* WRT */ String _str) {
        Runtime.Ref<String> str = new Runtime.Ref<>(_str);

        ModifyStringByRef(str);
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();

        text = "Initial Text";
        ModifyStringByValue(text);
        inOut.WriteString(text);
        inOut.WriteLn();
    }

    private void close() {
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        StringByValue instance = new StringByValue();
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
