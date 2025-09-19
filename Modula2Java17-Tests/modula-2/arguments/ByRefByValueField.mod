MODULE ByRefByValueField;

	IMPORT InOut;
	
	TYPE
		Point = RECORD x, y: INTEGER END;
	
	VAR
		point: Point;
		
		
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

END ByRefByValueField.