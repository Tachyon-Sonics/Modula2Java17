package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.array.ArrayAccess64;

public class ArrayTest {
    
    @Test
    public void testArrayAccessCard64() throws IOException {
        CompilerHelper helper = new CompilerHelper("array");
        helper.getCompilerOptions().setDataModel(DataModelType.STRICT_32_64);
        helper.compile("ArrayAccess64.mod");
        
        helper.assertCompilationResult(ArrayAccess64.class, "import ch.pitchtech.modula.library.*;");
    }

}
