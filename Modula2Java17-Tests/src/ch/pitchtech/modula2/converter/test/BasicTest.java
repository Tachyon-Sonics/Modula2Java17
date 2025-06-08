package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
    public void testCompileFractions() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Fractions.mod");
        Path unexpectedInOut = helper.getTargetDir().resolve("generated").resolve("test").resolve("InOut.java");
        assert !Files.isRegularFile(unexpectedInOut);
        helper.assertCompilationResult(Fractions.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(Fractions::main);
        executor.assertOutput(getClass(), "Fractions.txt", output);
    }

}
