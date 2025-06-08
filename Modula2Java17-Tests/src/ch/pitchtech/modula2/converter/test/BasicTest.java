package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;

import generated.test.Basic;
import generated.test.Fractions;

public class BasicTest {
    
    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }

    @Test
    public void testCompileBasicModule() throws IOException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Basic.mod");
        helper.assertCompilationResult(Basic.class, 
                "import ch.pitchtech.modula.library.*;"); // This import is not needed);
    }
    
    /*
     * Compile Fraction.mod taken from https://fruttenboel.nl/mhc/ ("Fractions").
     * It required InOut in the standard library.
     */
    @Test
    public void testCompileFractions() throws IOException {
        CompilerHelper helper = new CompilerHelper(); // TODO it shouldn't generate InOut.java skeleton
        helper.compile("Fractions.mod"); // TODO continue: compare generated result with expected, then run expected result and check output
        Path unexpectedInOut = helper.getTargetDir().resolve("generated").resolve("test").resolve("InOut.java");
        assert !Files.isRegularFile(unexpectedInOut);
        helper.assertCompilationResult(Fractions.class);
    }

}
