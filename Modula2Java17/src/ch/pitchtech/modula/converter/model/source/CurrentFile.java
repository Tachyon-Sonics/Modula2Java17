package ch.pitchtech.modula.converter.model.source;

import java.nio.file.Path;

public class CurrentFile {
    
    private final static ThreadLocal<Path> currentPath = new ThreadLocal<>();

    
    public static void setCurrentFile(Path file) {
        currentPath.set(file);
    }
    
    public static Path getCurrentFile() {
        return currentPath.get();
    }
}
