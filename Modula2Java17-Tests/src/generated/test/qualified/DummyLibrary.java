package generated.test.qualified;

public class DummyLibrary {

    private DummyLibrary() {
        instance = this; // Set early to handle circular dependencies
    }


    // CONST

    public static final int DummyConst = 42;


    // TYPE

    public static class DummyType { // RECORD

        public short x;
        public short y;


        public short getX() {
            return this.x;
        }

        public void setX(short x) {
            this.x = x;
        }

        public short getY() {
            return this.y;
        }

        public void setY(short y) {
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

    public short DummyVariable;


    public short getDummyVariable() {
        return this.DummyVariable;
    }

    public void setDummyVariable(short DummyVariable) {
        this.DummyVariable = DummyVariable;
    }


    // PROCEDURE

    public void DummyProcedure(int arg) {
    }

    public int DummyFunction(int arg) {
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
