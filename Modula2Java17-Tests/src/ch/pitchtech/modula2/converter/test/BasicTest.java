package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;

import generated.test.Basic;
import generated.test.Fractions;
import generated.test.Harmonic;
import generated.test.PowersOf2;

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
    
    @Test
    public void testCompileHarmonic() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Harmonic.mod");
        helper.assertCompilationResult(Harmonic.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String input = "100\n1000\n10000\nq\n";
        String output = executor.executeWithInput(Harmonic::main, input);
        executor.assertOutput(getClass(), "Harmonic.txt", output);
    }
    
    @Test
    public void testCompilePowerOf2() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("PowersOf2.mod");
        helper.assertCompilationResult(PowersOf2.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(PowersOf2::main);
        executor.assertOutput(getClass(), "PowersOf2.txt", output);
    }

}
