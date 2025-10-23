package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;

public class CommentTest {

    @Test
    public void testNestedComments() throws IOException {
        CompilerHelper helper = new CompilerHelper("test");
        helper.compile("NestedComments.mod");

        // If compilation succeeds without errors, the nested comments are working
    }

}
