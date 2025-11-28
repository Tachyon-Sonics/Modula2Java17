package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.After;
import org.junit.Test;

import generated.test.comment.CommentsInExpression;
import generated.test.comment.NestedComments;

public class CommentTest {

    @After
    public void cleanup() {
        CompilerHelper.cleanup();
    }

    @Test
    public void testNestedComments() throws IOException {
        CompilerHelper helper = new CompilerHelper("comment");
        helper.compile("NestedComments.mod");
        helper.assertCompilationResult(NestedComments.class, "import ch.pitchtech.modula.library.iso.*;");
    }

    @Test
    public void testCommentsInExpression() throws IOException {
        CompilerHelper helper = new CompilerHelper("comment");
        helper.compile("CommentsInExpression.mod");
        helper.assertCompilationResult(CommentsInExpression.class, "import ch.pitchtech.modula.library.iso.*;");
    }

}
