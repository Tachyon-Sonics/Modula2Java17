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
    
    private long startTime = System.nanoTime();


    // PROCEDURE

    public void ResetClock() {
        startTime = System.nanoTime();
    }
    
    private final static long PERIOD = 1000000000L / 60;

    public int UserTime() {
        long elapsed = System.nanoTime() - startTime;
        return (int) (elapsed / PERIOD);
    }


    public void begin() {

    }

    public void close() {

    }
}
