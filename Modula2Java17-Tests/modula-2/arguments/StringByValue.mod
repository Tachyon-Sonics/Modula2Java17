MODULE StringByValue;

	IMPORT InOut;


	VAR
		text: ARRAY[0..29] OF CHAR;


	PROCEDURE ModifyStringByRef(VAR str: ARRAY OF CHAR);
	BEGIN
		str := "Modified";
	END ModifyStringByRef;
	
	PROCEDURE ModifyStringByValue(str: ARRAY OF CHAR);
	BEGIN
		ModifyStringByRef(str);
	END ModifyStringByValue;
	
BEGIN

	text := "Initial Text";
	ModifyStringByValue(text);
	InOut.WriteString(text);
	InOut.WriteLn();

END StringByValue.