/*
* Top-level comment, definition
 */
package generated.test.qualified;

public class DummyLibrary {

    private DummyLibrary() {
        instance = this; // Set early to handle circular dependencies
    }


    // CONST

    /* Dummy const */
    public static final int DummyConst = 42;


    // TYPE

    /* Dummy type */
    public static class DummyType { // RECORD

        public int x;
        public int y;


        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }


        public void copyFrom(DummyType other) {
            this.x = other.x;
            this.y = other.y;
        }

        public DummyType newCopy() {
            DummyType copy = new DummyType();
            copy.copyFrom(this);
            return copy;
        }

    }


    // VAR

    /* Dummy variable */
    public int DummyVariable;
    public DummyType DummyTypeVariable = new DummyType();


    public int getDummyVariable() {
        return this.DummyVariable;
    }

    public void setDummyVariable(int DummyVariable) {
        this.DummyVariable = DummyVariable;
    }

    public DummyType getDummyTypeVariable() {
        return this.DummyTypeVariable;
    }

    public void setDummyTypeVariable(DummyType DummyTypeVariable) {
        this.DummyTypeVariable = DummyTypeVariable;
    }


    // PROCEDURE

    /*
    * Top-level comment, implementation
     */
    /* Dummy procedure */
    public void DummyProcedure(long arg) {
    }

    /* Dummy function */
    public long DummyFunction(long arg) {
        return arg;
    }

    /* Test function */
    public DummyType TestFunction(/* var */ DummyType arg) {
        return arg;
    }


    // Support

    private static DummyLibrary instance;

    public static DummyLibrary instance() {
        if (instance == null)
            new DummyLibrary(); // will set 'instance'
        return instance;
    }

    // Life-cycle

    public void begin() {
    }

    public void close() {
    }

}