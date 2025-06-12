package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Test;

import generated.test.qualified.DummyLibrary;
import generated.test.qualified.DummyModule;

public class ScopingTest {

    /**
     * Test compilation of 'DummyModule', using stuff from 'DummyLibrary' in a qualified manner
     * (qualified accesses used to be buggy)
     * @throws InvocationTargetException 
     */
    @Test
    public void testCompileBasicModule() throws IOException, InvocationTargetException {
        // Compile
        CompilerHelper helper = new CompilerHelper("qualified");
        helper.compile("DummyModule.mod");
        
        // Check compilation result
        helper.assertCompilationResult(DummyLibrary.class);
        helper.assertCompilationResult(DummyModule.class,
                "import ch.pitchtech.modula.library.*;");
        
        // Check that the resulting Java class runs without errors
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(DummyModule::main);
        assert output.isEmpty();
        
        /*
         * TODO (3) continue with:
         * [ok] qualified constant
         * [ok] qualified type
         * [ok] qualified var
         * [ok] qualified var of qualified type
         * [ok] qualified record item access: module.recordVar.recordItem
         * - qualified call of a var of procedure type ?
         */
    }

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }

}
