package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import generated.test.memory.StorageTest;
import generated.test.qualified.DummyLibrary;
import generated.test.qualified.DummyModule;

/*
 * TODO (2) in Storage.def, replace "ADDRESS" by "SYSTEM.ADDRESS"
 */
public class MemoryTest {
    
    /**
     * Test compilation of MemoryManagement.mod, that uses Storage's
     * ALLOCATE and DEALLOCATE.
     */
    @Test
    public void testCompileStorageTest() throws IOException, InvocationTargetException {
        // Compile
        CompilerHelper helper = new CompilerHelper("memory");
        helper.compile("StorageTest.mod");
        
        // Check compilation result
        helper.assertCompilationResult(StorageTest.class,
                "@SuppressWarnings(\"unused\")");
        
        // Check that the resulting Java class runs without errors
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(StorageTest::main);
        executor.assertOutput(getClass(), "StorageTest.txt", output);
    }

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }


}
