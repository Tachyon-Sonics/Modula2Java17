package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

public class BasicTest {

    @Test
    public void testCompileBasicModule() throws IOException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Basic.mod");

        Path generatedFile = helper.getTargetDir().resolve("generated").resolve("test").resolve("Basic.java");
        Path expectedFile = Path.of("src").resolve("generated").resolve("test").resolve("Basic.java");
        String generated = cleanup(Files.readString(generatedFile));
        String expected = cleanup(Files.readString(expectedFile));
        Assert.assertEquals(expected, generated);
    }
    
    /*
     * Compile Fraction.mod taken from https://fruttenboel.nl/mhc/ ("Fractions").
     * It required InOut in the standard library.
     */
    @Test
    public void testCompileFractions() throws IOException {
        CompilerHelper helper = new CompilerHelper(); // TODO it shouldn't generate InOut.java skeleton
        helper.compile("Fractions.mod"); // TODO continue: compare generated result with expected, then run expected result and check output
    }

    private static String cleanup(String content) {
        content = content.replace("\r\n", "\n").trim();
        return content;
    }
}
