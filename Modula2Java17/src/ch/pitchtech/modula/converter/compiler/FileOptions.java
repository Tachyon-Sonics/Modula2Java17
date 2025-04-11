package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;

public class FileOptions {
    
    private final Path m2sourceDir;
    private final Path targetMainDir;
    private final Path targetLibraryDir;
    
    
    public FileOptions(Path m2sourceDir, Path targetMainDir, Path targetLibraryDir) {
        this.m2sourceDir = m2sourceDir;
        this.targetMainDir = targetMainDir;
        this.targetLibraryDir = targetLibraryDir;
    }

    public Path getM2sourceDir() {
        return m2sourceDir;
    }
    
    public Path getTargetMainDir() {
        return targetMainDir;
    }

    public Path getTargetLibraryDir() {
        return targetLibraryDir;
    }

}
