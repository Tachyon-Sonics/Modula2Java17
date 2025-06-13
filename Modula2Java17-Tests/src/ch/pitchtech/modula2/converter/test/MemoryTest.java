package ch.pitchtech.modula2.converter.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.After;
import org.junit.Test;

import generated.test.memory.NewDisposeTest;
import generated.test.memory.NewDisposeTest2;
import generated.test.memory.StorageTest;

/*
 * TODO (2) in Storage.def, replace "ADDRESS" by "SYSTEM.ADDRESS" and fix problems
 * TODO (3) Try replacing SIZE by TSIZE
 * TODO (3) NEW on variable of another module (to test qualifier insertion)
 */
public class MemoryTest {
    
    /**
     * Test compilation of StorageTest.mod, that uses Storage's
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
    
    /**
     * Test compilation of NewDisposeTest.mod, that uses NEW and DISPOSE with
     * RECORD pointers.
     * Output is the same as StorageTest.mod
     */
    @Test
    public void testCompileNewDisposeTest() throws IOException, InvocationTargetException {
        // Compile
        CompilerHelper helper = new CompilerHelper("memory");
        helper.compile("NewDisposeTest.mod");
        
        // Check compilation result
        helper.assertCompilationResult(NewDisposeTest.class,
                "@SuppressWarnings(\"unused\")");
        
        // Check that the resulting Java class runs without errors
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(NewDisposeTest::main);
        executor.assertOutput(getClass(), "StorageTest.txt", output);
    }
    
    /**
     * Test compilation of NewDisposeTest.mod, that uses NEW and DISPOSE with
     * non-record pointers.
     * @throws IOException 
     */
    @Test
    public void testCompileNewDisposeTest2() throws IOException {
        // Compile
        CompilerHelper helper = new CompilerHelper("memory");
        helper.compile("NewDisposeTest2.mod");
        
        // Check compilation result
        helper.assertCompilationResult(NewDisposeTest2.class,
                "@SuppressWarnings(\"unused\")");
    }

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }


}
