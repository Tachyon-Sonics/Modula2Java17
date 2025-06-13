MODULE NewDisposeTest3;

    FROM Storage IMPORT ALLOCATE, DEALLOCATE;
    
    TYPE
        CharArr = ARRAY [0..29] OF CHAR;
        CharArrPtr = POINTER TO CharArr;
        
    VAR
        arr1: CharArrPtr;
        arr2: POINTER TO CharArr;
        arr3: POINTER TO ARRAY [0..29] OF CHAR;
    
BEGIN

    NEW(arr1);
    NEW(arr2);
    NEW(arr3);
    (*
    arr1^[0] := 'x';
    arr2^ := "Test";
    arr3^ := arr2^;
    *)

    DISPOSE(arr3);
    DISPOSE(arr2);
    DISPOSE(arr1);

END NewDisposeTest3.