MODULE DummyModule;

    IMPORT DummyLibrary;
    
    VAR
        result: LONGINT;
        temp: INTEGER;
        
        (* Qualified type access *)
        (* 
        test: DummyLibrary.DummyType;
        *)
    
BEGIN
    
    (* Qualified procedure call *)
    DummyLibrary.DummyProcedure(42);
    
    (* Qualified function call *)
    result := DummyLibrary.DummyFunction(42);
    
    (* Qualified constant access *)
    (*
    result := DummyLibrary.DummyConstant;
    *)
    
    (* Qualified var accesses, read, write *)
    temp := DummyLibrary.DummyVariable;
    DummyLibrary.DummyVariable := temp;
    
END DummyModule.