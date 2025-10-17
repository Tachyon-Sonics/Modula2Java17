package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.types.Constants;
import generated.test.types.DataModel;
import generated.test.types.DoubleAlias;
import generated.test.types.PointerArrayPointer;
import generated.test.types.Unsigned;
import generated.test.types.Unsigned16;
import generated.test.types.UnsignedStrict16;
import generated.test.types.UnsignedStrict32;

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
    
    @Test
    public void testConstants() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_16_32);
        helper.compile("Constants.mod");
        
        helper.assertCompilationResult(Constants.class, "import ch.pitchtech.modula.library.*;");
    }
    
    @Test
    public void testDataModel() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("DataModel.mod");
        
        helper.assertCompilationResult(DataModel.class, "import ch.pitchtech.modula.library.*;");
    }
    
    @Test
    public void testUnsigned() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("Unsigned.mod");
        
        helper.assertCompilationResult(Unsigned.class, "import ch.pitchtech.modula.library.*;");
    }

    @Test
    public void testUnsigned16() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_16_32);
        helper.compile("Unsigned.mod");
        
        helper.assertCompilationResult("Unsigned", Unsigned16.class, "import ch.pitchtech.modula.library.*;");
    }

//    @Test
//    public void testUnsignedStrict32() throws IOException {
//        CompilerHelper helper = new CompilerHelper("types");
//        helper.getCompilerOptions().setDataModel(DataModelType.STRICT_32_64);
//        helper.compile("Unsigned.mod");
//        
//        helper.assertCompilationResult("Unsigned", UnsignedStrict32.class, "import ch.pitchtech.modula.library.*;");
//    }
//
//    @Test
//    public void testUnsignedStrict16() throws IOException {
//        CompilerHelper helper = new CompilerHelper("types");
//        helper.getCompilerOptions().setDataModel(DataModelType.STRICT_16_32);
//        helper.compile("Unsigned.mod");
//        
//        helper.assertCompilationResult("Unsigned", UnsignedStrict16.class, "import ch.pitchtech.modula.library.*;");
//    }
//
}
