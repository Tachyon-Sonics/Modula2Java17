package ch.pitchtech.modula2.converter.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BasicTest.class,
    ScopingTest.class,
    MemoryTest.class,
    TypesTest.class,
    ArgumentsTest.class,
})
public class FastRootSuite {

}
