package ch.pitchtech.modula2.converter.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;

public class ExecuteHelper {
    
    @FunctionalInterface
    public static interface IMainMethod {
        
        public void main(String[] args) throws Exception;
        
    }
    
    /**
     * Execute a class' <tt>main</tt> method, and return its standard output
     */
    public String execute(IMainMethod mainMethod, String... args) throws InvocationTargetException {
        PrintStream previous = System.out;
        try {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(bOutput, true, StandardCharsets.UTF_8);
            System.setOut(stream);
            try {
                mainMethod.main(args);
            } catch (Exception ex) {
                throw new InvocationTargetException(ex);
            }
            stream.flush();
            return new String(bOutput.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(previous);
        }
    }

    /**
     * Execute a class' <tt>main</tt> method, and return its standard output
     * @param stdIn data to supply to standard input
     */
    public String executeWithInput(IMainMethod mainMethod, String stdIn, String... args) throws InvocationTargetException {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        try {
            InputStream input = new ByteArrayInputStream(stdIn.getBytes(StandardCharsets.UTF_8));
            System.setIn(input);
            
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
            PrintStream stream = new PrintStream(bOutput, true, StandardCharsets.UTF_8);
            System.setOut(stream);
            try {
                mainMethod.main(args);
            } catch (Exception ex) {
                throw new InvocationTargetException(ex);
            }
            stream.flush();
            return new String(bOutput.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(previousOut);
            System.setIn(previousIn);
        }
    }

    /**
     * Assert the output from {@link #execute(IMainMethod, String...)} matches the expected one.
     * <p>
     * The expected output is assumed to be a ressource, that is loaded using a class and a name.
     * @param fromClass a class in the same package as the resource containing the expected output
     * @param expectedResourceName the name of the resource containing the expected output
     * @param actualOutput the actual output produced by {@link #execute(IMainMethod, String...)}
     */
    public void assertOutput(Class<?> fromClass, String expectedResourceName, String actualOutput) throws IOException {
        InputStream input = fromClass.getResourceAsStream(expectedResourceName);
        try (input) {
            byte[] data = input.readAllBytes();
            String expectedOutput = new String(data, StandardCharsets.UTF_8);
            expectedOutput = CompilerHelper.cleanup(expectedOutput);
            actualOutput = CompilerHelper.cleanup(actualOutput);
            Assert.assertEquals(expectedOutput, actualOutput);
        }
    }

}
