package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;

import ch.pitchtech.modula.converter.compiler.DataModelType;
import generated.test.set.BitSetAccess;
import generated.test.set.EnumSetAccess;
import generated.test.set.SetAccess;
import generated.test.set.SetAccess64;

public class SetTest {
    

    @Test
    public void testSetAccess() throws IOException {
        CompilerHelper helper = new CompilerHelper("set");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_16_32);
        helper.compile("SetAccess.mod");
        
        helper.assertCompilationResult(SetAccess.class, "import ch.pitchtech.modula.library.*;");
    }
    
    @Test
    public void testBitSetAccess() throws IOException {
        CompilerHelper helper = new CompilerHelper("set");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_16_32);
        helper.compile("BitSetAccess.mod");
        
        helper.assertCompilationResult(BitSetAccess.class, "import ch.pitchtech.modula.library.*;");
    }

    @Test
    public void testSetAccessCard64() throws IOException {
        CompilerHelper helper = new CompilerHelper("set");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_32_64);
        helper.compile("SetAccess64.mod");
        
        helper.assertCompilationResult(SetAccess64.class, "import ch.pitchtech.modula.library.*;");
    }
    
    @Test
    public void testEnumSetAccess() throws IOException {
        CompilerHelper helper = new CompilerHelper("set");
        helper.getCompilerOptions().setDataModel(DataModelType.LOOSE_16_32);
        helper.compile("EnumSetAccess.mod");
        
        helper.assertCompilationResult(EnumSetAccess.class, "import ch.pitchtech.modula.library.*;");
    }
    
}
