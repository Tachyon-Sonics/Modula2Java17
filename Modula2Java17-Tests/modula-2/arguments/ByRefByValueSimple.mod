MODULE ByRefByValueSimple;

	IMPORT InOut;
	
	VAR
		value: INTEGER;
		
		
	PROCEDURE ModifyByRef(VAR value: INTEGER);
	BEGIN
		value := 42;
	END ModifyByRef;
	
	PROCEDURE ModifyByValue(value: INTEGER);
	BEGIN
		value := 42;
	END ModifyByValue;
	
	PROCEDURE ModifyByRefByRef(VAR value: INTEGER);
	BEGIN
		ModifyByRef(value);
	END ModifyByRefByRef;

	PROCEDURE ModifyByRefByValue(VAR value: INTEGER);
	BEGIN
		ModifyByValue(value);
	END ModifyByRefByValue;

	PROCEDURE ModifyByValueByRef(value: INTEGER);
	BEGIN
		ModifyByRef(value);
	END ModifyByValueByRef;

	PROCEDURE ModifyByValueByValue(value: INTEGER);
	BEGIN
		ModifyByValue(value);
	END ModifyByValueByValue;


BEGIN

	value := 10;
	ModifyByValueByValue(value);
	ModifyByRefByValue(value);
	ModifyByValueByRef(value);
	InOut.WriteInt(value, 2);
	InOut.WriteLn();
	ModifyByRefByRef(value);
	InOut.WriteInt(value, 2);
	InOut.WriteLn();

END ByRefByValueSimple.