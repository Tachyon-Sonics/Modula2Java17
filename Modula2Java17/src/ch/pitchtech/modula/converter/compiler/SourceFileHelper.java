package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class SourceFileHelper {
    
    private final static Set<String> DEFINITION_EXTENSIONS = Set.of(".def", ".md");
    private final static Set<String> IMPLEMENTATION_EXTENSIONS = Set.of(".mod", ".mi");
    

    public static boolean isDefinition(SourceFile sourceFile) {
        for (String extension : DEFINITION_EXTENSIONS) {
            if (sourceFile.getPath().toString().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Lookup the given source file. If it exists, return it. Else, look in all source folders configured
     * in {@link FileOptions#getM2sourceDirs()} and return the first one that match. Else return the
     * given path unmodified.
     */
    private static Path lookup(Path dir, String fileName, FileOptions fileOptions, Set<String> extensions) {
        for (String extension : extensions) {
            Path path = dir.resolve(fileName + extension);
            if (Files.isRegularFile(path)) {
                return path;
            }
        }
        for (String extension : extensions) {
            for (Path directory : fileOptions.getM2sourceDirs()) {
                Path altPath = directory.resolve(fileName + extension);
                if (Files.isRegularFile(altPath)) {
                    return altPath;
                }
            }
        }
        return null;
    }
    
    /**
     * Lookup the source DEFINITION file (*.def) based on the current file being parsed and
     * the imported module name (FROM ... IMPORT, or IMPORT ...)^
     * <p>
     * Returns <tt>null</tt> if importing the SYSTEM module
     */
    public static SourceFile lookupDefinition(SourceFile currentFile, String importedModuleName, FileOptions fileOptions) {
        if (importedModuleName.equals("SYSTEM"))
            return null;
        Path path = lookup(currentFile.getPath().getParent(), importedModuleName, fileOptions, DEFINITION_EXTENSIONS);
        if (path == null || !Files.isRegularFile(path)) {
            throw new CompilationException(null, "File {0}.def (or .md) not found in {1} (resolved: {2}), imported by {3}", 
                    importedModuleName, fileOptions.getM2sourceDirs(), fileOptions.getM2sourceDirsAbsolute(), currentFile.getPath());
        }
        return new SourceFile(path);
    }
    
    /**
     * Given an IMPLEMENTATION source file, lookup the corresponding DEFINITION source file
     */
    public static SourceFile lookupDefinition(SourceFile implementation, FileOptions fileOptions) {
        String modName = implementation.getPath().getFileName().toString();
        String baseName = null;
        for (String extension : IMPLEMENTATION_EXTENSIONS) {
            if (modName.endsWith(extension))
                baseName = modName.substring(0, modName.length() - extension.length());
        }
        Path path = lookup(implementation.getPath().getParent(), baseName, fileOptions, DEFINITION_EXTENSIONS);
        if (path != null && Files.isRegularFile(path)) {
            return new SourceFile(path);
        }
        throw new CompilationException(null, "File not found: {0}.def (or .md), definition for {1}", baseName, implementation.getPath());
    }
    
    /**
     * Given an DEFINITION (*.def) source file, lookup the corresponding IMPLEMENTATION (*.mod) source file.
     * <p>
     * Returns <tt>null</tt> if no IMPLEMENTATION (*.mod) is found
     */
    public static SourceFile lookupImplementation(SourceFile definition, FileOptions fileOptions) {
        String defName = definition.getPath().getFileName().toString();
        String baseName = null;
        for (String extension : DEFINITION_EXTENSIONS) {
            if (defName.endsWith(extension))
                baseName = defName.substring(0, defName.length() - extension.length());
        }
        Path path = lookup(definition.getPath().getParent(), baseName, fileOptions, IMPLEMENTATION_EXTENSIONS);
        if (path != null && Files.isRegularFile(path)) {
            return new SourceFile(path);
        }
        return null;
    }
    
}
