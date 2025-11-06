package ch.pitchtech.modula.library.iso;

import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Conversions {
    
    private final static Conversions instance = new Conversions();
    
    
    private Conversions() {
        
    }
    
    public static Conversions instance() {
        return instance;
    }
    
    public void ConvertCardinal(long num, long length, IRef<String> str) {
        String result = "" + num;
        if (result.length() < length) {
            StringBuilder prefix = new StringBuilder();
            for (int k = 0; k < length - result.length(); k++)
                prefix.append(' ');
            result = prefix.toString() + result;
        }
        str.set(result);
    }
    
    public void CardToStr(long num, IRef<String> str, IRef<Boolean> ok) {
        String result = "" + num;
        str.set(result);
        ok.set(true);
    }
    
    public void begin() {
        
    }
    
    public void close() {
        
    }

}
