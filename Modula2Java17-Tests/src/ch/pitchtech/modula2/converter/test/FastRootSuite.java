package ch.pitchtech.modula2.converter.test;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import generated.test.Basic;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BasicTest.class,
    ScopingTest.class,
    MemoryTest.class,
})
public class FastRootSuite {

    @Test
    public void testQualifiedImport() throws IOException {
        CompilerHelper helper = new CompilerHelper();
        helper.compile("Basic.mod");
        helper.assertCompilationResult(Basic.class, 
                "import ch.pitchtech.modula.library.*;"); // This import is not needed);
    }
    

}
