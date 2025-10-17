package generated.test.memory;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


@SuppressWarnings("unused")
public class NewDisposeTest2 {

    // TYPE

    private static class IntPtr extends Runtime.Ref<Integer> {
    }



    // VAR

    private int[] arr1 /* POINTER */;
    private int[] arr2 /* POINTER */;
    private int[] arr3 /* POINTER */;
    private Runtime.IRef<Integer> ptr1 /* POINTER */;
    private Runtime.IRef<Integer> ptr2 /* POINTER */;
    private Runtime.IRef<Runtime.IRef<Integer>> ptrptr /* POINTER */;
    private Runtime.IRef<Runtime.IRef<Integer>> ptrptr2 /* POINTER */;
    private Runtime.IRef<Runtime.IRef<Runtime.IRef<Integer>>> ptrptrptr /* POINTER */;
    private Runtime.IRef<int[]> arrptr /* POINTER */;


    public int[] getArr1() {
        return this.arr1;
    }

    public void setArr1(int[] arr1) {
        this.arr1 = arr1;
    }

    public int[] getArr2() {
        return this.arr2;
    }

    public void setArr2(int[] arr2) {
        this.arr2 = arr2;
    }

    public int[] getArr3() {
        return this.arr3;
    }

    public void setArr3(int[] arr3) {
        this.arr3 = arr3;
    }

    public Runtime.IRef<Integer> getPtr1() {
        return this.ptr1;
    }

    public void setPtr1(Runtime.IRef<Integer> ptr1) {
        this.ptr1 = ptr1;
    }

    public Runtime.IRef<Integer> getPtr2() {
        return this.ptr2;
    }

    public void setPtr2(Runtime.IRef<Integer> ptr2) {
        this.ptr2 = ptr2;
    }

    public Runtime.IRef<Runtime.IRef<Integer>> getPtrptr() {
        return this.ptrptr;
    }

    public void setPtrptr(Runtime.IRef<Runtime.IRef<Integer>> ptrptr) {
        this.ptrptr = ptrptr;
    }

    public Runtime.IRef<Runtime.IRef<Integer>> getPtrptr2() {
        return this.ptrptr2;
    }

    public void setPtrptr2(Runtime.IRef<Runtime.IRef<Integer>> ptrptr2) {
        this.ptrptr2 = ptrptr2;
    }

    public Runtime.IRef<Runtime.IRef<Runtime.IRef<Integer>>> getPtrptrptr() {
        return this.ptrptrptr;
    }

    public void setPtrptrptr(Runtime.IRef<Runtime.IRef<Runtime.IRef<Integer>>> ptrptrptr) {
        this.ptrptrptr = ptrptrptr;
    }

    public Runtime.IRef<int[]> getArrptr() {
        return this.arrptr;
    }

    public void setArrptr(Runtime.IRef<int[]> arrptr) {
        this.arrptr = arrptr;
    }


    // Life Cycle

    private void begin() {
        Storage.instance().begin();

        arr1 = new int[21];
        arr2 = new int[21];
        arr3 = new int[21];
        ptr1 = new Runtime.Ref<>(0);
        ptr2 = new Runtime.Ref<>(0);
        ptrptr = new Runtime.Ref<>();
        ptrptr.set(new Runtime.Ref<>(0));
        ptrptr2 = new Runtime.Ref<>();
        ptrptr2.set(new Runtime.Ref<>(0));
        arrptr = new Runtime.Ref<>();
        arrptr.set(new int[21]);
        ptrptrptr = new Runtime.Ref<>();
        ptrptrptr.set(new Runtime.Ref<>());
        ptrptrptr.get().set(new Runtime.Ref<>(0));
        ptrptrptr.get().set(null);
        ptrptrptr.set(null);
        ptrptrptr = null;
        arrptr.set(null);
        arrptr = null;
        ptrptr2.set(null);
        ptrptr2 = null;
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