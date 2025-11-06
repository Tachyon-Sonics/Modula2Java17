package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.types.Card16Test;
import generated.test.types.Card8Test;
import generated.test.types.Constants;
import generated.test.types.DataModel;
import generated.test.types.DataModel16;
import generated.test.types.DataModelStrict16;
import generated.test.types.DataModelStrict32;
import generated.test.types.DoubleAlias;
import generated.test.types.PointerArrayPointer;
import generated.test.types.UnsignedCast;
import generated.test.types.UnsignedCast32;
import generated.test.types.UnsignedTest;
import generated.test.types.UnsignedTest16;
import generated.test.types.UnsignedTestLoose32;
import generated.test.types.UnsignedTestStrict16;
import generated.test.types.UnsignedTestStrict32;

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
                "import ch.pitchtech.modula.library.iso.*;", // Unnecessary import
                "    @SuppressWarnings(\"unused\")");
    }
    
    @Test
    public void testPointerToArrayOfPointers() throws IOException, InvocationTargetException {
        // Compile
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("PointerArrayPointer.mod");

        // Check compilation result
        helper.assertCompilationResult(PointerArrayPointer.class,
                "import ch.pitchtech.modula.library.iso.*;", // Unnecessary import
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
        helper.getCompilerOptions().setDataModel(DataModelType.DM_16);
        helper.compile("Constants.mod");
        
        helper.assertCompilationResult(Constants.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testDataModel() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("DataModel.mod");
        
        helper.assertCompilationResult(DataModel.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testDataModel16() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_16);
        helper.compile("DataModel.mod");
        
        helper.assertCompilationResult("DataModel", DataModel16.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testDataModelStrict32() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_32);
        helper.compile("DataModel.mod");
        
        helper.assertCompilationResult("DataModel", DataModelStrict32.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testDataModelStrict16() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_16);
        helper.compile("DataModel.mod");
        
        helper.assertCompilationResult("DataModel", DataModelStrict16.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testUnsigned() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.compile("UnsignedTest.mod");
        
        helper.assertCompilationResult(UnsignedTest.class, "import ch.pitchtech.modula.library.iso.*;");
    }

    @Test
    public void testUnsigned16() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_16);
        helper.compile("UnsignedTest.mod");
        
        helper.assertCompilationResult("UnsignedTest", UnsignedTest16.class, "import ch.pitchtech.modula.library.iso.*;");
    }

    @Test
    public void testUnsignedStrict32() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_32);
        helper.compile("UnsignedTest.mod");
        
        helper.assertCompilationResult("UnsignedTest", UnsignedTestStrict32.class, "import ch.pitchtech.modula.library.iso.*;");
    }

    @Test
    public void testUnsignedLoose32() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_32);
        helper.getCompilerOptions().getExactUnsignedTypes().clear();
        helper.compile("UnsignedTest.mod");
        
        helper.assertCompilationResult("UnsignedTest", UnsignedTestLoose32.class, "import ch.pitchtech.modula.library.iso.*;");
    }

    @Test
    public void testUnsignedStrict16() throws IOException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_16);
        helper.compile("UnsignedTest.mod");
        
        helper.assertCompilationResult("UnsignedTest", UnsignedTestStrict16.class, "import ch.pitchtech.modula.library.iso.*;");
    }
    
    @Test
    public void testCard8() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_16);
        helper.compile("Card8Test.mod");
        
        helper.assertCompilationResult(Card8Test.class);

        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(Card8Test::main);
        // 255 / 17 as CARD8 (unsigned byte), should be 15
        assertEquals("15", output.trim());
    }

    @Test
    public void testCard16() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_16);
        helper.compile("Card16Test.mod");
        
        helper.assertCompilationResult(Card16Test.class);

        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(Card16Test::main);
        // 65535 / 255 as CARD16 (unsigned short), should be 257
        assertEquals("257", output.trim());
    }
    
    @Test
    public void testUnsignedCast16() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_16);
        helper.compile("UnsignedCast.mod");
        
        helper.assertCompilationResult(UnsignedCast.class);

        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(UnsignedCast::main);
        String[] lines = output.split("\n");
        assertEquals("42", lines[0].trim());
        assertEquals("42", lines[1].trim());
    }

    @Test
    public void testUnsignedCast32() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("types");
        helper.getCompilerOptions().setDataModel(DataModelType.DM_STRICT_32);
        helper.compile("UnsignedCast.mod");
        
        helper.assertCompilationResult("UnsignedCast", UnsignedCast32.class);

        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(UnsignedCast32::main);
        String[] lines = output.split("\n");
        assertEquals("42", lines[0].trim());
        assertEquals("42", lines[1].trim());
    }

}
