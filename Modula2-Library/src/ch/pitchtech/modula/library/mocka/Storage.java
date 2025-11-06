package ch.pitchtech.modula.library.mocka;

import ch.pitchtech.modula.runtime.Runtime.IRef;
import ch.pitchtech.modula.runtime.StorageImpl;

public class Storage {

    private final static Storage instance = new Storage();


    private Storage() {

    }

    public static Storage instance() {
        return instance;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void ALLOCATE(IRef<?> adr, long size) {
        Object result = StorageImpl.allocate((int) size);
        ((IRef) adr).set(result);
    }

    public void DEALLOCATE(IRef<?> adr, long size) {
        adr.set(null);
    }
    
    public void begin() {
        
    }
    
    public void close() {
        
    }

}
