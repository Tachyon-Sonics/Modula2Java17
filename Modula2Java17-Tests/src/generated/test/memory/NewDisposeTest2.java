package generated.test.memory;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


@SuppressWarnings("unused")
public class NewDisposeTest2 {

    // TYPE

    private static class IntPtr extends Runtime.Ref<Short> {
    }



    // VAR

    private short[] arr1 /* POINTER */;
    private short[] arr2 /* POINTER */;
    private short[] arr3 /* POINTER */;
    private Runtime.IRef<Short> ptr1 /* POINTER */;
    private Runtime.IRef<Short> ptr2 /* POINTER */;
    private Runtime.IRef<Runtime.IRef<Short>> ptrptr /* POINTER */;
    private Runtime.IRef<short[]> arrptr /* POINTER */;


    public short[] getArr1() {
        return this.arr1;
    }

    public void setArr1(short[] arr1) {
        this.arr1 = arr1;
    }

    public short[] getArr2() {
        return this.arr2;
    }

    public void setArr2(short[] arr2) {
        this.arr2 = arr2;
    }

    public short[] getArr3() {
        return this.arr3;
    }

    public void setArr3(short[] arr3) {
        this.arr3 = arr3;
    }

    public Runtime.IRef<Short> getPtr1() {
        return this.ptr1;
    }

    public void setPtr1(Runtime.IRef<Short> ptr1) {
        this.ptr1 = ptr1;
    }

    public Runtime.IRef<Short> getPtr2() {
        return this.ptr2;
    }

    public void setPtr2(Runtime.IRef<Short> ptr2) {
        this.ptr2 = ptr2;
    }

    public Runtime.IRef<Runtime.IRef<Short>> getPtrptr() {
        return this.ptrptr;
    }

    public void setPtrptr(Runtime.IRef<Runtime.IRef<Short>> ptrptr) {
        this.ptrptr = ptrptr;
    }

    public Runtime.IRef<short[]> getArrptr() {
        return this.arrptr;
    }

    public void setArrptr(Runtime.IRef<short[]> arrptr) {
        this.arrptr = arrptr;
    }


    // Life Cycle

    private void begin() {
        Storage.instance().begin();

        arr1 = new short[21];
        arr2 = new short[21];
        arr3 = new short[21];
        ptr1 = new Runtime.Ref<>((short) 0);
        ptr2 = new Runtime.Ref<>((short) 0);
        ptrptr = new Runtime.Ref<>();
        ptrptr.set(new Runtime.Ref<>((short) 0));
        arrptr = new Runtime.Ref<>();
        arrptr.set(new short[21]);
        arrptr.set(null);
        arrptr = null;
        ptrptr.set(null);
        ptrptr = null;
        ptr2 = null;
        ptr1 = null;
        arr3 = null;
        arr2 = null;
        arr1 = null;
    }

    private void close() {
        Storage.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        NewDisposeTest2 instance = new NewDisposeTest2();
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