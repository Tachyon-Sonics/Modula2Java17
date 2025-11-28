(*
 * Top-level comment, definition
 *)
DEFINITION MODULE DummyLibrary;

    (* Dummy const *)
    CONST DummyConst = 42;
    
    TYPE
        (* Dummy type *)
        DummyType = RECORD x, y: INTEGER END;
        
    VAR
        (* Dummy variable *)
        DummyVariable : INTEGER;
        DummyTypeVariable : DummyType;
       
    (* Dummy procedure definition *) 
    PROCEDURE DummyProcedure(arg: LONGINT);
    
    (* Dummy function definition *) 
    PROCEDURE DummyFunction(arg: LONGINT): LONGINT;
    
    PROCEDURE TestFunction(VAR arg: DummyType): DummyType;

END DummyLibrary.