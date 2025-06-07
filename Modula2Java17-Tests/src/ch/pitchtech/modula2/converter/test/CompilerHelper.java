package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

/**
 * Helper for invoking the compiler in tests.
 */
public class CompilerHelper {
    
    private final FileOptions fileOptions;
    private final CompilerOptions compilerOptions;
    private final Path targetDir;
    

    /**
     * Create a helper. Add the "modula-2" folder from both this "Modula2Java17-Test" project
     * and from the "Modula2-Library" project as modula2 source folders.
     * <p>
     * Sets the target folder as a temporary one, which can be retrieved by {@link #getTargetDir()}.
     * <p>
     * Sets the Java package for the generated code to "generated.test"
     */
    public CompilerHelper() throws IOException {
        targetDir = Files.createTempDirectory("compiled"); // TODO delete on exit
        fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of("modula-2"));
        fileOptions.addM2sourceDir(getModulaLibraryPath());
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("generated.test");
        compilerOptions.setTargetPackageLib("generated.test");
    }
    
    // The "modula-2" folder in the "Modula2-Library" project
    private static Path getModulaLibraryPath() {
        Path sourcePath = Path.of("modula-2");
        Path testProjectDir = sourcePath.toAbsolutePath().getParent();
        Path modulaLibraryProjectDir = testProjectDir.getParent().resolve("Modula2-Library");
        return modulaLibraryProjectDir.resolve("modula-2");
    }
    
    public FileOptions getFileOptions() {
        return fileOptions;
    }
    
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }
    
    public Path getTargetDir() {
        return targetDir;
    }

    /**
     * Compile the give Modula-2 file, assumed to be in the "modula-2" folder in this "Modula2Java17-Test"
     * project. The resulting Java files are placed in {@link #getTargetDir()}
     * @param fileName name of the Modula-2 source file
     */
    public void compile(String fileName) throws IOException {
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        SourceFile sourceFile = new SourceFile(Path.of("modula-2").resolve(fileName));
        compiler.compile(sourceFile);
    }
    
}
