package ch.pitchtech.modula.library.iso;

import ch.pitchtech.modula.runtime.Runtime.IRef;

public class Strings {
    
    private final static Strings instance = new Strings();
    
    
    private Strings() {
        
    }
    
    public static Strings instance() {
        return instance;
    }
    
    public void Append(String source, IRef<String> destination) {
        destination.set(destination.get() + source);
    }
    
    public void begin() {
        
    }
    
    public void close() {
        
    }

}
