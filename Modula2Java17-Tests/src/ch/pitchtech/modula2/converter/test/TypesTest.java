package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import generated.test.types.DoubleAlias;
import generated.test.types.PointerArrayPointer;

public class TypesTest {
    
    /**
     * Test compilation of DoubleAlias.mod, that uses two levels of
     * type aliases.
     */
    @Test
    public void testDoubleAlias() throws IOException {
        // Compile
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("DoubleAlias.mod");

        // Check compilation result
        helper.assertCompilationResult(DoubleAlias.class,
                "import ch.pitchtech.modula.library.*;", // Unnecessary import
                "    @SuppressWarnings(\"unused\")");
    }
    
    @Test
    public void testPointerToArrayOfPointers() throws IOException, InvocationTargetException {
        // Compile
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("PointerArrayPointer.mod");

        // Check compilation result
        helper.assertCompilationResult(PointerArrayPointer.class,
                "import ch.pitchtech.modula.library.*;", // Unnecessary import
                "    @SuppressWarnings(\"unused\")",
                "    @SuppressWarnings(\"unchecked\")");
        
        // Check that the resulting Java class runs without errors
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(PointerArrayPointer::main);
        assertEquals("", output);
    }

}
