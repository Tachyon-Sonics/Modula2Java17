package generated.test.memory;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class NewDisposeTest3 {

    // VAR

    private String arr1 /* POINTER */;
    private String arr2 /* POINTER */;
    private String arr3 /* POINTER */;


    public String getArr1() {
        return this.arr1;
    }

    public void setArr1(String arr1) {
        this.arr1 = arr1;
    }

    public String getArr2() {
        return this.arr2;
    }

    public void setArr2(String arr2) {
        this.arr2 = arr2;
    }

    public String getArr3() {
        return this.arr3;
    }

    public void setArr3(String arr3) {
        this.arr3 = arr3;
    }


    // Life Cycle

    private void begin() {
        Storage.instance().begin();

        arr1 = "";
        arr2 = "";
        arr3 = "";
        arr1 = Runtime.setChar(arr1, 0, 'x');
        arr2 = "Test";
        arr3 = Runtime.copyOf(true, arr2);
        arr3 = null;
        arr2 = null;
        arr1 = null;
    }

    private void close() {
        Storage.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        NewDisposeTest3 instance = new NewDisposeTest3();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}