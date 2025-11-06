package ch.pitchtech.modula.converter.model.source;

import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CurrentFile {
    
    private final static ThreadLocal<SourceFile> currentPath = new ThreadLocal<>();

    
    public static void setCurrentFile(SourceFile file) {
        currentPath.set(file);
    }
    
    public static SourceFile getCurrentFile() {
        return currentPath.get();
    }
}
