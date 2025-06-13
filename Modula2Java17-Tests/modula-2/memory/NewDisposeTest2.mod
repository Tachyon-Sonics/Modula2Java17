MODULE NewDisposeTest2;

    FROM Storage IMPORT ALLOCATE, DEALLOCATE;
    
    TYPE
        IntArr = ARRAY [0..20] OF INTEGER;
        IntArrPtr = POINTER TO IntArr;
        IntPtr = POINTER TO INTEGER;
        
    VAR
        arr1: IntArrPtr;
        arr2: POINTER TO IntArr;
        arr3: POINTER TO ARRAY [0..20] OF INTEGER;
        ptr1: IntPtr;
        ptr2: POINTER TO INTEGER;
        ptrptr: POINTER TO IntPtr;
    
BEGIN

    NEW(arr1);
    NEW(arr2);
    NEW(arr3);
    NEW(ptr1);
    NEW(ptr2);
    NEW(ptrptr);
    NEW(ptrptr^);
    
    DISPOSE(ptrptr^);
    DISPOSE(ptrptr);
    DISPOSE(ptr2);
    DISPOSE(ptr1);
    DISPOSE(arr3);
    DISPOSE(arr2);
    DISPOSE(arr1);

END NewDisposeTest2.