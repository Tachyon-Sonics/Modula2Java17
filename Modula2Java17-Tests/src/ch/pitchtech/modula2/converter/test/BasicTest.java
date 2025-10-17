package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.Basic;
import generated.test.EscapeSeq;
import generated.test.Fractions;
import generated.test.Fractions32;
import generated.test.Harmonic;
import generated.test.PowersOf2;
import generated.test.Primes;

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
     * Compile various files taken from https://fruttenboel.nl/mhc/.
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
    
    /**
     * Same as {@link #testCompileFractions()}, but with the 32-bit data model
     */
    @Test
    public void testCompileFractions32() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper();
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_32);
        helper.compile("Fractions32.mod");
        helper.assertCompilationResult(Fractions32.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(Fractions32::main);
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
    
    @Test
    public void testCompilePrimes() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Primes.mod");
        helper.assertCompilationResult(Primes.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(Primes::main);
        executor.assertOutput(getClass(), "Primes.txt", output);
    }
    
    @Test
    public void testCompileEscapeSeq() throws IOException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("EscapeSeq.mod");
        helper.assertCompilationResult(EscapeSeq.class);
    }

}
