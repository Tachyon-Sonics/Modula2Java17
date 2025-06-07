package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileOptions {
    
    private final List<Path> m2sourceDirs = new ArrayList<>();
    private Path targetMainDir;
    private Path targetLibraryDir;
    
    
    public FileOptions() {
        
    }
    
    public FileOptions(Path m2sourceDir, Path targetMainDir, Path targetLibraryDir) {
        this.m2sourceDirs.add(m2sourceDir);
        this.targetMainDir = targetMainDir;
        this.targetLibraryDir = targetLibraryDir;
    }
    
    public void addM2sourceDir(Path m2sourceDir) {
        m2sourceDirs.add(m2sourceDir);
    }
    
    public List<Path> getM2sourceDirs() {
        return m2sourceDirs;
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
