MODULE StorageTest;

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

    ALLOCATE(point, SIZE(Point));
    point^.x := 10;
    point^.y := 20;
    Write("(");
    WriteInt(point^.x, 2);
    Write(",");
    WriteInt(point^.y, 2);
    Write(")");
    WriteLn;
    DEALLOCATE(point, SIZE(Point));
    
    ALLOCATE(rectangle, SIZE(Rectangle));
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
    WriteLn;

END StorageTest.