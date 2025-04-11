package ch.pitchtech.modula.converter.utils;


public class Logger {
    
    private static int verboseLevel = 1;

    
    public static int getVerboseLevel() {
        return verboseLevel;
    }

    public static void setVerboseLevel(int verboseLevel) {
        Logger.verboseLevel = verboseLevel;
    }
    
    public static void log(int level, String message, Object... args) {
        if (level <= verboseLevel) {
            System.out.println(StringUtils.format(message, args));
        }
    }

}
