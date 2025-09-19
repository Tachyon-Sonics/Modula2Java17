MODULE ByRefByValueArray;

	IMPORT InOut;
	
	TYPE
		MyArr = ARRAY[0..9] OF INTEGER;
	
	VAR
		value: MyArr;
		
		
	PROCEDURE ModifyByRef(VAR value: MyArr);
		VAR
			arr2: MyArr;
	BEGIN
		arr2[0] := 42;
		value := arr2;
	END ModifyByRef;
	
	PROCEDURE ModifyByValue(value: MyArr);
		VAR
			arr2: MyArr;
	BEGIN
		arr2[0] := 42;
		value := arr2;
	END ModifyByValue;
	
	PROCEDURE ModifyByRefByRef(VAR value: MyArr);
	BEGIN
		ModifyByRef(value);
	END ModifyByRefByRef;

	PROCEDURE ModifyByRefByValue(VAR value: MyArr);
	BEGIN
		ModifyByValue(value);
	END ModifyByRefByValue;

	PROCEDURE ModifyByValueByRef(value: MyArr);
	BEGIN
		ModifyByRef(value);
	END ModifyByValueByRef;

	PROCEDURE ModifyByValueByValue(value: MyArr);
	BEGIN
		ModifyByValue(value);
	END ModifyByValueByValue;

BEGIN

	value[0] := 10;
	ModifyByValueByValue(value);
	ModifyByRefByValue(value);
	ModifyByValueByRef(value);
	InOut.WriteInt(value[0], 2);
	InOut.WriteLn();
	ModifyByRefByRef(value);
	InOut.WriteInt(value[0], 2);
	InOut.WriteLn();

END ByRefByValueArray.