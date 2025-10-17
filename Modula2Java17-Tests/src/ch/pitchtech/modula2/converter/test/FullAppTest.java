package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
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
        targetDir.toFile().deleteOnExit();
        FileOptions fileOptions = new FileOptions();
        Path appModulaPath = getAppModulaPath("ChaosCastle", "Grotte");
        if (!Files.isDirectory(appModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Grotte' project from the 'ChaosCastle' repository cannot be found at " + appModulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        Path libModulaPath = getLibModulaPath("ChaosCastle");
        if (!Files.isDirectory(libModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Library' project from the 'ChaosCastle' repository cannot be found at " + libModulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        fileOptions.addM2sourceDir(appModulaPath);
        fileOptions.addM2sourceDir(libModulaPath);
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.grotte");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        compilerOptions.setDataModel(DataModelType.DM_16);
        CompilerHelper helper = new CompilerHelper(targetDir, fileOptions, compilerOptions);
        helper.compile(appModulaPath.resolve("Grotte.mod"));
        
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
        targetDir.toFile().deleteOnExit();
        FileOptions fileOptions = new FileOptions();
        Path appModulaPath = getAppModulaPath("ChaosCastle", "ChaosCastle");
        if (!Files.isDirectory(appModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'ChaosCastle' project from the 'ChaosCastle' repository cannot be found at " + appModulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        Path libModulaPath = getLibModulaPath("ChaosCastle");
        if (!Files.isDirectory(libModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Library' project from the 'ChaosCastle' repository cannot be found at " + libModulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        fileOptions.addM2sourceDir(appModulaPath);
        fileOptions.addM2sourceDir(libModulaPath);
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        compilerOptions.setDataModel(DataModelType.DM_16);
        CompilerHelper helper = new CompilerHelper(targetDir, fileOptions, compilerOptions);
        helper.compile(appModulaPath.resolve("ChaosCastle.mod"));
        
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

    /**
     * Assuming the "Modula2Chess" project exists, this will compile it fully, and compare
     * the generated Java files with the actual one.
     * @throws IOException 
     */
    @Test
    public void testCompileChess() throws IOException {
        // Compile "Modula2Chess" project
        Path targetDir = Files.createTempDirectory("compiled");
        targetDir.toFile().deleteOnExit();
        FileOptions fileOptions = new FileOptions();
        Path appModulaPath = getAppModulaPath("Modula2-Examples", "Modula2Chess", "src");
        if (!Files.isDirectory(appModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Modula2Chess' project from the 'Modula2-Examples' repository cannot be found at " + appModulaPath.toString()
                    + "\nMake sure you checkout this repo/project in order to run this test."
                    + "\nOr launch the FastRootSuite instead of the RootSuite to skip tests requiring other projects.");
        }
        Path libModulaPath = getStdLibModulaPath();
        if (!Files.isDirectory(libModulaPath)) {
            throw new UnsupportedOperationException(
                    "The 'Modula2-Library' project cannot be found at " + libModulaPath.toString());
        }
        fileOptions.addM2sourceDir(appModulaPath);
        fileOptions.addM2sourceDir(libModulaPath);
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.pitchtech.modula.chess");
        compilerOptions.setTargetPackageLib("ch.pitchtech.modula.library");
        compilerOptions.setCharset(StandardCharsets.ISO_8859_1);
        compilerOptions.setDataModel(DataModelType.DM_32);
        CompilerHelper helper = new CompilerHelper(targetDir, fileOptions, compilerOptions);
        helper.compile(appModulaPath.resolve("Modula2Chess.mi"));
        
        // Compare each generated files with actual ones
        Path generatedCodePath = targetDir.resolve("ch").resolve("pitchtech").resolve("modula").resolve("chess");
        Path actualCodePath = getJavaPath("Modula2-Examples", "Modula2Chess", "java")
                .resolve("ch").resolve("pitchtech").resolve("modula").resolve("chess");
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
        assertEquals("Number of generated files do not match", 4, nbGenerated.get());
    }

    private static Path getProjectPath(String repositoryName, String projectName) {
        Path sourcePath = Path.of("modula2");
        Path testProjectPath = sourcePath.toAbsolutePath().getParent();
        Path testRepoPath = testProjectPath.getParent();
        Path repoPath = testRepoPath.getParent().resolve(repositoryName);
        Path projectPath = repoPath.resolve(projectName);
        return projectPath;
    }
    
    // The "modula2" folder in the given project
    private static Path getAppModulaPath(String repositoryName, String projectName) {
        return getAppModulaPath(repositoryName, projectName, "modula2");
    }
    
    private static Path getAppModulaPath(String repositoryName, String projectName, String modulaFolder) {
        Path projectPath = getProjectPath(repositoryName, projectName);
        return projectPath.resolve(modulaFolder);
    }

    // The "modula2" folder in the given project
    private static Path getLibModulaPath(String repositoryName) {
        Path projectPath = getProjectPath(repositoryName, "Library");
        return projectPath.resolve("modula2");
    }

    // The "modula-2" folder in the given project
    private static Path getStdLibModulaPath() {
        Path projectPath = getProjectPath("Modula2Java17", "Modula2-Library");
        return projectPath.resolve("modula-2");
    }

    // The "src" folder in the given project
    private static Path getJavaPath(String repositoryName, String projectName) {
        return getJavaPath(repositoryName, projectName, "src");
    }
    
    private static Path getJavaPath(String repositoryName, String projectName, String srcFolder) {
        Path projectPath = getProjectPath(repositoryName, projectName);
        return projectPath.resolve(srcFolder);
    }

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }

}
