package ch.pitchtech.modula.library;

public class Clock {

    private static Clock instance;


    private Clock() {
        instance = this; // Set early to handle circular dependencies
    }

    public static Clock instance() {
        if (instance == null)
            new Clock(); // will set 'instance'
        return instance;
    }


    // PROCEDURE

    public void ResetClock() {
        // todo implement ResetClock
        throw new UnsupportedOperationException("Not implemented: ResetClock");
    }

    public short UserTime() {
        // todo implement UserTime
        throw new UnsupportedOperationException("Not implemented: UserTime");
    }


    public void begin() {

    }

    public void close() {

    }
}
