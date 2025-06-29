MODULE DummyModule;

    IMPORT DummyLibrary;
    
    VAR
        result: LONGINT;
        temp: INTEGER;
        
        (* Qualified type access *)
        test: DummyLibrary.DummyType;
        
    PROCEDURE TestProc(arg: DummyLibrary.DummyType): DummyLibrary.DummyType;
    BEGIN
        RETURN arg;
    END TestProc;
    
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
    
    (* Qualified arg and result *)
    test := TestProc(test);
    
END DummyModule.