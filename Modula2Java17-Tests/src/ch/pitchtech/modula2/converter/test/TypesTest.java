package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;

import generated.test.types.DoubleAlias;

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

}
