MODULE DoubleAlias;

    TYPE
        IntPtr = POINTER TO INTEGER;
        Alias1 = IntPtr;
        Alias2 = Alias1;
        
    PROCEDURE Dummy(arg: Alias2);
    BEGIN
    END Dummy;

END DoubleAlias.