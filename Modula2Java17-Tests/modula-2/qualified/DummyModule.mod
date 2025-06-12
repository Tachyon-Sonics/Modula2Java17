MODULE DummyModule;

    IMPORT DummyLibrary;
    
    VAR
        result: LONGINT;
        temp: INTEGER;
        
        (* Qualified type access *)
        test: DummyLibrary.DummyType;
    
BEGIN
    
    (* Qualified procedure call *)
    DummyLibrary.DummyProcedure(42);
    
    (* Qualified function call *)
    result := DummyLibrary.DummyFunction(42);
    
    (* Qualified constant access *)
    result := DummyLibrary.DummyConst;
    
    (* Qualified var accesses, read, write *)
    temp := DummyLibrary.DummyVariable;
    DummyLibrary.DummyVariable := temp;
    
    (* Access to var of qualified type *)
    test.x := 10;
    temp := test.x;
    
    (* Access to qualified var of qualified type *)
    temp := DummyLibrary.DummyTypeVariable.x;
    DummyLibrary.DummyTypeVariable.y := temp;
    DummyLibrary.DummyTypeVariable := test;
    test := DummyLibrary.DummyTypeVariable;
    
END DummyModule.