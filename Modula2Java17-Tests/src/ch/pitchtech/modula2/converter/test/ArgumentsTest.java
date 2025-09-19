package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Ignore;
import org.junit.Test;

import generated.test.arguments.ByRefByValueArray;
import generated.test.arguments.ByRefByValueArrayElt;
import generated.test.arguments.ByRefByValueField;
import generated.test.arguments.ByRefByValueRecord;
import generated.test.arguments.ByRefByValueSimple;
import generated.test.arguments.StringByValue;

public class ArgumentsTest {
    
    @Test
    public void testStringByValue() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("StringByValue.mod");
        
        helper.assertCompilationResult(StringByValue.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(StringByValue::main);
        
        /*
         * We have an initial pass-by-value, with a nested pass-by-ref.
         * Hence the value is NOT modified, because of the initial pass-by-value:
         */
        assertEquals("Initial Text", output.trim());
    }
    
    @Test
    public void testByRefByValueSimple() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("ByRefByValueSimple.mod");
        
        helper.assertCompilationResult(ByRefByValueSimple.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(ByRefByValueSimple::main);
        executor.assertOutput(getClass(), "ByRefByValue.txt", output);
    }

    @Test
    public void testByRefByValueRecord() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("ByRefByValueRecord.mod");
        
        helper.assertCompilationResult(ByRefByValueRecord.class, 
                "    @SuppressWarnings(\"unused\")");
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(ByRefByValueRecord::main);
        executor.assertOutput(getClass(), "ByRefByValue.txt", output);
    }

    @Test
    public void testByRefByValueField() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("ByRefByValueField.mod");
        
        helper.assertCompilationResult(ByRefByValueField.class, 
                "    @SuppressWarnings(\"unused\")");
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(ByRefByValueField::main);
        executor.assertOutput(getClass(), "ByRefByValue.txt", output);
    }

    @Ignore // TODO (1) fix
    @Test
    public void testByRefByValueArray() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("ByRefByValueArray.mod");
        
        helper.assertCompilationResult(ByRefByValueArray.class, 
                "    @SuppressWarnings(\"unused\")");
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(ByRefByValueArray::main);
        executor.assertOutput(getClass(), "ByRefByValue.txt", output);
    }

    @Test
    public void testByRefByValueArrayElt() throws IOException, InvocationTargetException {
        CompilerHelper helper = new CompilerHelper("arguments");
        helper.compile("ByRefByValueArrayElt.mod");
        
        helper.assertCompilationResult(ByRefByValueArrayElt.class);
        
        ExecuteHelper executor = new ExecuteHelper();
        String output = executor.execute(ByRefByValueArrayElt::main);
        executor.assertOutput(getClass(), "ByRefByValue.txt", output);
    }

}
