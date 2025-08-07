package ch.pitchtech.modula.library;

public class RealInOut {
    
    private final static RealInOut instance = new RealInOut();
    
    public boolean Done;
    
    
    private RealInOut() {
        
    }
    
    public boolean isDone() {
        return Done;
    }
    
    public void setDone(boolean done) {
        Done = done;
    }

    public static RealInOut instance() {
        return instance;
    }
    
    public void WriteLongReal(double value, long n) {
        String str = "" + value;
        if (str.length() < n) {
            StringBuilder prefix = new StringBuilder();
            for (int k = 0; k < n - str.length(); k++)
                prefix.append(' ');
            str = prefix.toString() + str;
        }
        System.out.print(str);
    }
    
    public void begin() {
        
    }
    
    public void close() {
        
    }

}
