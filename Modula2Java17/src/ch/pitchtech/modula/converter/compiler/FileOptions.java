package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileOptions {
    
    private static final ThreadLocal<FileOptions> current = new ThreadLocal<>();

    private final List<Path> m2sourceDirs = new ArrayList<>();
    private Path targetMainDir;
    private Path targetLibraryDir;
    private String standardLibrary; // "iso" or "mocka"
    private String libraryArchive = "Modula2-Library.jar";
    
    
    public FileOptions() {
        
    }
    
    static void set(FileOptions options) {
        if (options != null) {
            if (current.get() != null)
                throw new IllegalArgumentException("FileOptions was already set");
            current.set(options);
        } else {
            current.remove();
        }
    }
    
    public static FileOptions get() {
        return current.get();
    }
    
    public void addM2sourceDir(Path m2sourceDir) {
        m2sourceDirs.add(m2sourceDir);
    }
    
    public List<Path> getM2sourceDirs() {
        return m2sourceDirs;
    }
    
    public List<Path> getM2sourceDirsAbsolute() {
        return m2sourceDirs.stream().map(Path::toAbsolutePath).map(Path::normalize).toList();
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

    public String getStandardLibrary() {
        return standardLibrary;
    }
    
    /**
     * Set the name of a built-in standard library to use, like "iso" or "mocka".
     * <p>
     * This automatically sets {@link CompilerOptions#setTargetPackageLib(String)}
     */
    public void setStandardLibrary(String standardLibrary) {
        this.standardLibrary = standardLibrary;
    }

    public String getLibraryArchive() {
        return libraryArchive;
    }

    public void setLibraryArchive(String libraryArchive) {
        this.libraryArchive = libraryArchive;
    }

}
