package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;

/**
 * These tests are quite brute-force: they compile full applications, and compare the result
 * with the already existing one.
 * <p>
 * This also mean that if the compiler is modified in such a way the generated code changes,
 * the Java classes of the underlying applications must be generated again.
 * <p>
 * The tests will be ignored if the respective application's project, "Grotte" and "ChaosCastle",
 * are not found.
 */
public class FullAppTest {
    
    /**
     * Assuming the "Grotte" project exists, this will compile it fully, and compare
     * the generated Java files with the actual one.
     * @throws IOException 
     */
    @Test
    public void testCompileGrotte() throws IOException {
        // Compile "Grotte" project
        Path targetDir = Files.createTempDirectory("compiled");
        FileOptions fileOptions = new FileOptions();
        Path modulaPath = getModulaPath("ChaosCastle", "Grotte");
        if (!Files.isDirectory(modulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Grotte' project from the 'ChaosCastle' repository cannot be found at " + modulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        fileOptions.addM2sourceDir(modulaPath);
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.grotte");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        CompilerHelper helper = new CompilerHelper(targetDir, fileOptions, compilerOptions);
        helper.compile(modulaPath.resolve("Grotte.mod"));
        
        // Compare each generated files with actual ones
        Path generatedCodePath = targetDir.resolve("ch").resolve("chaos").resolve("grotte");
        Path actualCodePath = getJavaPath("ChaosCastle", "Grotte").resolve("ch").resolve("chaos").resolve("grotte");
        AtomicInteger nbGenerated = new AtomicInteger();
        Files.list(generatedCodePath).forEach((Path generatedFile) -> {
            if (generatedFile.getFileName().toString().endsWith(".java")) {
                nbGenerated.incrementAndGet();
                Path actualFile = actualCodePath.resolve(generatedFile.getFileName());
                assert Files.isRegularFile(actualFile);
                try {
                    String generated = Files.readString(generatedFile);
                    String actual = Files.readString(actualFile);
                    generated = CompilerHelper.cleanup(generated);
                    actual = CompilerHelper.cleanup(actual);
                    Assert.assertEquals("Differences in " + actualFile.getFileName(), actual, generated);
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }
        });
        assertEquals("Number of generated files do not match", 5, nbGenerated.get());
    }

    /**
     * Assuming the "ChaosCastle" project exists, this will compile it fully, and compare
     * the generated Java files with the actual one.
     * @throws IOException 
     */
    @Test
    public void testCompileChaosCastle() throws IOException {
        // Compile "ChaosCastle" project
        Path targetDir = Files.createTempDirectory("compiled");
        FileOptions fileOptions = new FileOptions();
        Path modulaPath = getModulaPath("ChaosCastle", "ChaosCastle");
        if (!Files.isDirectory(modulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'ChaosCastle' project from the 'ChaosCastle' repository cannot be found at " + modulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        fileOptions.addM2sourceDir(modulaPath);
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        CompilerHelper helper = new CompilerHelper(targetDir, fileOptions, compilerOptions);
        helper.compile(modulaPath.resolve("ChaosCastle.mod"));
        
        // Compare each generated files with actual ones
        Path generatedCodePath = targetDir.resolve("ch").resolve("chaos").resolve("castle");
        Path actualCodePath = getJavaPath("ChaosCastle", "ChaosCastle").resolve("ch").resolve("chaos").resolve("castle");
        AtomicInteger nbGenerated = new AtomicInteger();
        Files.list(generatedCodePath).forEach((Path generatedFile) -> {
            if (generatedFile.getFileName().toString().endsWith(".java")) {
                nbGenerated.incrementAndGet();
                Path actualFile = actualCodePath.resolve(generatedFile.getFileName());
                assert Files.isRegularFile(actualFile);
                try {
                    String generated = Files.readString(generatedFile);
                    String actual = Files.readString(actualFile);
                    generated = CompilerHelper.cleanup(generated);
                    actual = CompilerHelper.cleanup(actual);
                    Assert.assertEquals("Differences in " + actualFile.getFileName(), actual, generated);
                } catch (IOException ex) {
                    throw new IOError(ex);
                }
            }
        });
        assertEquals("Number of generated files do not match", 27, nbGenerated.get());
    }

    private static Path getProjectPath(String repositoryName, String projectName) {
        Path sourcePath = Path.of("modula2");
        Path testProjectPath = sourcePath.toAbsolutePath().getParent();
        Path testRepoPath = testProjectPath.getParent();
        Path repoPath = testRepoPath.getParent().resolve(repositoryName);
        Path projectPath = repoPath.resolve(projectName);
        return projectPath;
    }
    
    // The "modula-2" folder in the given project
    private static Path getModulaPath(String repositoryName, String projectName) {
        Path projectPath = getProjectPath(repositoryName, projectName);
        return projectPath.resolve("modula2");
    }

    // The "modula-2" folder in the given project
    private static Path getJavaPath(String repositoryName, String projectName) {
        Path projectPath = getProjectPath(repositoryName, projectName);
        return projectPath.resolve("src");
    }

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }

}
