MODULE ArrayAccess64;

	VAR
		array: ARRAY[0..9] OF INTEGER;
		index: LONGCARD;
		
BEGIN

	index := 4;
	array[index] := 42;
	index := array[4];

END ArrayAccess64.
