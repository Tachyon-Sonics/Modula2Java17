package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class BasicTest {

    @Test
    public void testCompileBasicModule() throws IOException { // TODO (0) create helpers and continue
        Path targetDir = Files.createTempDirectory("compiled");
        FileOptions fileOptions = new FileOptions();
        fileOptions.setM2sourceDir(Path.of("modula-2"));
        fileOptions.setTargetMainDir(targetDir);
        fileOptions.setTargetLibraryDir(targetDir);
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("generated.test");
        compilerOptions.setTargetPackageLib("generated.test");
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        SourceFile sourceFile = new SourceFile(fileOptions.getM2sourceDir().resolve("Basic.mod"));
        compiler.compile(sourceFile);

        Path generatedFile = targetDir.resolve("generated").resolve("test").resolve("Basic.java");
        Path expectedFile = Path.of("src").resolve("generated").resolve("test").resolve("Basic.java");
        String generated = cleanup(Files.readString(generatedFile));
        String expected = cleanup(Files.readString(expectedFile));
        Assert.assertEquals(expected, generated);
    }

    private static String cleanup(String content) {
        content = content.replace("\r\n", "\n").trim();
        return content;
    }
}
