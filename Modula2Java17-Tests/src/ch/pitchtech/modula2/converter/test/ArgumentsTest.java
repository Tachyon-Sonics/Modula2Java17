package ch.pitchtech.modula2.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

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

}
