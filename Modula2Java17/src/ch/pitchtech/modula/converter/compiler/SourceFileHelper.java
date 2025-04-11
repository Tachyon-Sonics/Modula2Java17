package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Files;
import java.nio.file.Path;

public class SourceFileHelper { // TODO (2) also handle .md and .mi and .m extensions

    public static boolean isDefinition(SourceFile sourceFile) {
        return sourceFile.getPath().toString().toLowerCase().endsWith(".def");
    }
    
    /**
     * Lookup the source DEFINITION file (*.def) based on the current file being parsed and
     * the imported module name (FROM ... IMPORT, or IMPORT ...)^
     * <p>
     * Returns <tt>null</tt> if importing the SYSTEM module
     */
    public static SourceFile lookupDefinition(SourceFile currentFile, String importedModuleName) {
        if (importedModuleName.equals("SYSTEM"))
            return null;
        Path path = currentFile.getPath().getParent().resolve(importedModuleName + ".def");
        if (!Files.isRegularFile(path)) {
            throw new CompilationException(null, "File not found: {0}, imported by {1}", path, currentFile.getPath());
        }
        return new SourceFile(path);
    }
    
    /**
     * Given an IMPLEMENTATION source file, lookup the corresponding DEFINITION source file
     */
    public static SourceFile lookupDefinition(SourceFile implementation) {
        String modName = implementation.getPath().getFileName().toString();
        String defName = modName.replace(".mod", ".def");
        Path path = implementation.getPath().getParent().resolve(defName);
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
    public static SourceFile lookupImplementation(SourceFile definition) {
        String defName = definition.getPath().getFileName().toString();
        String modName = defName.replace(".def", ".mod");
        Path path = definition.getPath().getParent().resolve(modName);
        if (Files.isRegularFile(path)) {
            return new SourceFile(path);
        }
        return null;
    }
    
}
