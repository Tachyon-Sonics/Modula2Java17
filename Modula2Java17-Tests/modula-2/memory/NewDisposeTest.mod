MODULE NewDisposeTest;

    FROM Storage IMPORT ALLOCATE, DEALLOCATE;
    FROM InOut IMPORT WriteInt, WriteString, Write, WriteLn;
    
    TYPE
        Point = RECORD x, y: INTEGER END;
        PointPtr = POINTER TO Point;
        Rectangle = RECORD
            topLeft: Point;
            bottomRightPtr: PointPtr
        END;
        RectanglePtr = POINTER TO Rectangle;
        
    VAR
        point: PointPtr;
        rectangle: RectanglePtr;
    
BEGIN

    NEW(point);
    point^.x := 10;
    point^.y := 20;
    Write("(");
    WriteInt(point^.x, 2);
    Write(",");
    WriteInt(point^.y, 2);
    Write(")");
    WriteLn;
    DISPOSE(point);
    
    NEW(rectangle);
    rectangle^.topLeft.x := 42;
    rectangle^.topLeft.y := 84;
    Write("(");
    WriteInt(rectangle^.topLeft.x, 2);
    Write(",");
    WriteInt(rectangle^.topLeft.y, 2);
    Write(")");
    WriteLn;
    IF rectangle^.bottomRightPtr = NIL THEN
        WriteString("NIL");
        WriteLn;
    END;
    NEW(rectangle^.bottomRightPtr);
    DISPOSE(rectangle^.bottomRightPtr);
    DISPOSE(rectangle);

END NewDisposeTest.