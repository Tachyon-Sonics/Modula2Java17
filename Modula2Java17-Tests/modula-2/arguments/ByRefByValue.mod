MODULE ByRefByValue;

	IMPORT InOut;
	
	TYPE
		Point = RECORD x, y: INTEGER END;
	
	VAR
		value: INTEGER;
		point: Point;
		
		
	PROCEDURE ModifyByRefInt(VAR value: INTEGER);
	BEGIN
		value := 42;
	END ModifyByRefInt;
	
	PROCEDURE ModifyByValueInt(value: INTEGER);
	BEGIN
		value := 42;
	END ModifyByValueInt;
	
	PROCEDURE ModifyByRefByRefInt(VAR value: INTEGER);
	BEGIN
		ModifyByRefInt(value);
	END ModifyByRefByRefInt;

	PROCEDURE ModifyByRefByValueInt(VAR value: INTEGER);
	BEGIN
		ModifyByValueInt(value);
	END ModifyByRefByValueInt;

	PROCEDURE ModifyByValueByRefInt(value: INTEGER);
	BEGIN
		ModifyByRefInt(value);
	END ModifyByValueByRefInt;

	PROCEDURE ModifyByValueByValueInt(value: INTEGER);
	BEGIN
		ModifyByValueInt(value);
	END ModifyByValueByValueInt;


	PROCEDURE ModifyByRef(VAR value: Point);
	BEGIN
		value.x := 42;
	END ModifyByRef;
	
	PROCEDURE ModifyByValue(value: Point);
	BEGIN
		value.x := 42;
	END ModifyByValue;
	
	PROCEDURE ModifyByRefByRef(VAR value: Point);
	BEGIN
		ModifyByRef(value);
	END ModifyByRefByRef;

	PROCEDURE ModifyByRefByValue(VAR value: Point);
	BEGIN
		ModifyByValue(value);
	END ModifyByRefByValue;

	PROCEDURE ModifyByValueByRef(value: Point);
	BEGIN
		ModifyByRef(value);
	END ModifyByValueByRef;

	PROCEDURE ModifyByValueByValue(value: Point);
	BEGIN
		ModifyByValue(value);
	END ModifyByValueByValue;

BEGIN

	value := 10;
	ModifyByValueByValueInt(value);
	ModifyByRefByValueInt(value);
	ModifyByValueByRefInt(value);
	InOut.WriteInt(value, 2);
	InOut.WriteLn();
	ModifyByRefByRefInt(value);
	InOut.WriteInt(value, 2);
	InOut.WriteLn();

	point.x := 10;
	point.y := 10;
	ModifyByValueByValue(point);
	ModifyByRefByValue(point);
	ModifyByValueByRef(point);
	InOut.WriteInt(point.x, 2);
	InOut.WriteLn();
	ModifyByRefByRef(point);
	InOut.WriteInt(point.x, 2);
	InOut.WriteLn();

END ByRefByValue.