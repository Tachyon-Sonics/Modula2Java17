IMPLEMENTATION MODULE DummyLibrary;

    PROCEDURE DummyProcedure(arg: LONGINT);
    BEGIN
    END DummyProcedure;
    
    PROCEDURE DummyFunction(arg: LONGINT): LONGINT;
    BEGIN
        RETURN arg;
    END DummyFunction;

END DummyLibrary.