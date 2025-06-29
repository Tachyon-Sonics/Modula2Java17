package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Files;
import java.nio.file.Path;

public class SourceFileHelper { // TODO (2) also handle .md and .mi and .m extensions

    public static boolean isDefinition(SourceFile sourceFile) {
        return sourceFile.getPath().toString().toLowerCase().endsWith(".def");
    }
    
    /**
     * Lookup the given source file. If it exists, return it. Else, look in all source folders configured
     * in {@link FileOptions#getM2sourceDirs()} and return the first one that match. Else return the
     * given path unmodified.
     */
    private static Path lookup(Path path, FileOptions fileOptions) {
        if (Files.isRegularFile(path)) {
            return path;
        }
        for (Path directory : fileOptions.getM2sourceDirs()) {
            Path altPath = directory.resolve(path.getFileName());
            if (Files.isRegularFile(altPath)) {
                return altPath;
            }
        }
        return path;
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
        Path path = currentFile.getPath().getParent().resolve(importedModuleName + ".def");
        path = lookup(path, fileOptions);
        if (!Files.isRegularFile(path)) {
            throw new CompilationException(null, "File {0} not found in {1}, imported by {2}", 
                    path.getFileName(), fileOptions.getM2sourceDirs(), currentFile.getPath());
        }
        return new SourceFile(path);
    }
    
    /**
     * Given an IMPLEMENTATION source file, lookup the corresponding DEFINITION source file
     */
    public static SourceFile lookupDefinition(SourceFile implementation, FileOptions fileOptions) {
        String modName = implementation.getPath().getFileName().toString();
        String defName = modName.replace(".mod", ".def");
        Path path = implementation.getPath().getParent().resolve(defName);
        path = lookup(path, fileOptions);
        if (Files.isRegularFile(path)) {
            return new SourceFile(path);
        }
        throw new CompilationException(null, "File not found: {0}, definition for {1}", path, implementation.getPath());
    }
    
    /**
     * Given an DEFINITION (*.def) source file, lookup the corresponding IMPLEMENTATION (*.mod) source file.
     * <p>
     * Returns <tt>null</tt> if no IMPLEMENTATION (*.mod) is found
     */
    public static SourceFile lookupImplementation(SourceFile definition, FileOptions fileOptions) {
        String defName = definition.getPath().getFileName().toString();
        String modName = defName.replace(".def", ".mod");
        Path path = definition.getPath().getParent().resolve(modName);
        path = lookup(path, fileOptions);
        if (Files.isRegularFile(path)) {
            return new SourceFile(path);
        }
        return null;
    }
    
}
