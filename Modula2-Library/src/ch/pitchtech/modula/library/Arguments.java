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

    public void GetArgs(/* VAR */ Runtime.IRef<Integer> numArgs, /* VAR */ Runtime.IRef<String[]> argTable) {
        String[] args = Runtime.getArgs();
        String[] nameAndArgs = new String[args.length + 1];
        nameAndArgs[0] = Runtime.getAppNameOrDefault();
        if (args.length > 0)
            System.arraycopy(args, 0, nameAndArgs, 1, args.length);
        numArgs.set(nameAndArgs.length);
        argTable.set(nameAndArgs);
    }


    public void begin() {

    }

    public void close() {

    }
}
