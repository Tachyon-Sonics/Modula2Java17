package ch.pitchtech.modula.library;

import ch.pitchtech.modula.runtime.Runtime;


public class Arguments {

    private static Arguments instance;


    private Arguments() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Arguments instance() {
        if (instance == null)
            new Arguments(); // will set 'instance'
        return instance;
    }


    // PROCEDURE

    public void GetArgs(/* VAR */ Runtime.IRef<Short> numArgs, /* VAR */ Runtime.IRef<String[]> argTable) {
        // todo implement GetArgs
        throw new UnsupportedOperationException("Not implemented: GetArgs");
    }


    public void begin() {

    }

    public void close() {

    }
}
