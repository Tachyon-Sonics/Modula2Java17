MODULE PointerArrayPointer;

    TYPE
    	CardPtr = POINTER TO CARDINAL;
    	CardPtrArray = ARRAY[0..12] OF CardPtr;
    	Buggy = POINTER TO CardPtrArray;
    	
    VAR
    	buggy: Buggy;
    	
BEGIN

	NEW(buggy);
	NEW(buggy^[0]);
	buggy^[0]^ := 42;
	
	DISPOSE(buggy^[0]);
	DISPOSE(buggy);

END PointerArrayPointer.