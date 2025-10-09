package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.set.SetAccess64;

public class SetTest {

    @Test
    public void testSetAccessCard64() throws IOException {
        CompilerHelper helper = new CompilerHelper("set");
        helper.getCompilerOptions().setDataModel(DataModelType.STRICT_32_64);
        helper.compile("SetAccess64.mod");
        
        helper.assertCompilationResult(SetAccess64.class, "import ch.pitchtech.modula.library.*;");
    }
    
    // TODO (1) Does not work in we declare directly VAR set : SET OF [0..63]


}
