package ch.pitchtech.modula2.converter.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.Assert;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

/**
 * Helper for invoking the compiler in tests.
 */
public class CompilerHelper {
    
    private final static List<Path> temporaryDirs = new ArrayList<>();
    
    private final FileOptions fileOptions;
    private final CompilerOptions compilerOptions;
    private final Path targetDir;
    
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(CompilerHelper::cleanup));
    }
    

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
        temporaryDirs.add(targetDir);
        fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of("modula-2"));
        fileOptions.addM2sourceDir(getModulaLibraryPath());
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(getJavaLibraryPath());
        compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("generated.test");
        compilerOptions.setTargetPackageLib("ch.pitchtech.modula.library");
    }
    
    // The "modula-2" folder in the "Modula2-Library" project
    private static Path getModulaLibraryPath() {
        Path sourcePath = Path.of("modula-2");
        Path testProjectDir = sourcePath.toAbsolutePath().getParent();
        Path modulaLibraryProjectDir = testProjectDir.getParent().resolve("Modula2-Library");
        return modulaLibraryProjectDir.resolve("modula-2");
    }
    
    // The "src" folder in the "Modula2-Library" project
    private static Path getJavaLibraryPath() {
        Path sourcePath = Path.of("modula-2");
        Path testProjectDir = sourcePath.toAbsolutePath().getParent();
        Path modulaLibraryProjectDir = testProjectDir.getParent().resolve("Modula2-Library");
        return modulaLibraryProjectDir.resolve("src");
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
    
    /**
     * Assert that a compiled Java class matches the expected result.
     * <p>
     * The compiled class is ecpected to be in {@link #getTargetDir()}, whereas the expected
     * result is expected to be in this project (usually in the <tt>generated</tt> package)
     * @param expectedClass the expected result, from this project
     * @param ignoreLines full lines of code (without terminating new line character) to drop from the generated
     * file before comparing. They usually correspond to useless stuff that makes a warning
     */
    public void assertCompilationResult(Class<?> expectedClass, String... ignoreLines) throws IOException {
        String className = expectedClass.getSimpleName();
        String packageName = expectedClass.getPackageName();
        
        // Retrieve generated file
        Path generatedFile = getTargetDir()
                .resolve(packageName.replace('.', File.separatorChar))
                .resolve(className + ".java");
        String generated = cleanup(Files.readString(generatedFile));
        // Remove any line to ignore
        for (String ignoreLine : ignoreLines) {
            if (generated.contains(ignoreLine + "\n")) {
                generated = generated.replace(ignoreLine + "\n", "");
            } else {
                assert false : "Line to ignore not found:\n" + ignoreLine;
            }
        }
        
        // Retrieve expected file
        Path expectedFile = Path.of("src")
                .resolve(packageName.replace('.', File.separatorChar))
                .resolve(className + ".java");
        String expected = cleanup(Files.readString(expectedFile));
        
        // Compare
        Assert.assertEquals(expected, generated);
    }
    
    /**
     * Normalize new lines
     */
    private static String cleanup(String content) {
        content = content.replace("\r\n", "\n").trim();
        return content;
    }
    
    /**
     * Delete files created by the compiler
     */
    public static void cleanup() {
        for (Path dir : temporaryDirs) {
            try (Stream<Path> walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            } catch (IOException ex) {
                ex.printStackTrace();
            }        
        }
        temporaryDirs.clear();
    }

}
