package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;

public class FileOptions {
    
    private Path m2sourceDir;
    private Path targetMainDir;
    private Path targetLibraryDir;
    
    
    public FileOptions() {
        
    }
    
    public FileOptions(Path m2sourceDir, Path targetMainDir, Path targetLibraryDir) {
        this.m2sourceDir = m2sourceDir;
        this.targetMainDir = targetMainDir;
        this.targetLibraryDir = targetLibraryDir;
    }

    public Path getM2sourceDir() {
        return m2sourceDir;
    }
    
    public void setM2sourceDir(Path m2sourceDir) {
        this.m2sourceDir = m2sourceDir;
    }
    
    public Path getTargetMainDir() {
        return targetMainDir;
    }
    
    public void setTargetMainDir(Path targetMainDir) {
        this.targetMainDir = targetMainDir;
    }
    
    public Path getTargetLibraryDir() {
        return targetLibraryDir;
    }

    public void setTargetLibraryDir(Path targetLibraryDir) {
        this.targetLibraryDir = targetLibraryDir;
    }

}
