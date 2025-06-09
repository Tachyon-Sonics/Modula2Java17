MODULE DummyModule;

    IMPORT DummyLibrary;
    
    VAR
        result: LONGINT;
    
BEGIN
    
    DummyLibrary.DummyProcedure(42);
    result := DummyLibrary.DummyFunction(42);
    
END DummyModule.