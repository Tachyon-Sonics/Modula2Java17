package ch.pitchtech.modula2.converter.test;

/**
 * These tests are quite brute-force: they compile full applications, and compare the result
 * with the already existing one.
 * <p>
 * This also mean that if the compiler is modified in such a way the generated code changes,
 * the Java classes of the underlying applications must be generated again.
 * <p>
 * The tests will be ignored if the respective application's project, "Grotte" and "ChaosCastle",
 * are not found.
 */
public class FullAppTest {
    
    /**
     * Assuming the "Grotte" project exists, this will compile it fully, and compare
     * the generated Java files with the actual one.
     */
    public void testCompileGrotte() {
        
    }

}
