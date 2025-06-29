DEFINITION MODULE DummyLibrary;

    CONST DummyConst = 42;
    
    TYPE
        DummyType = RECORD x, y: INTEGER END;
        
    VAR
        DummyVariable : INTEGER;
        DummyTypeVariable : DummyType;
        
    PROCEDURE DummyProcedure(arg: LONGINT);
    
    PROCEDURE DummyFunction(arg: LONGINT): LONGINT;
    
    PROCEDURE TestFunction(VAR arg: DummyType): DummyType;

END DummyLibrary.