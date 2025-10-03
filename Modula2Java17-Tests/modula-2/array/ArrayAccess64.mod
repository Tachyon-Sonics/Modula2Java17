MODULE ArrayAccess64;

	VAR
		array: ARRAY[0..9] OF INTEGER;
		x, y: LONGCARD;
		
BEGIN

	x := 4;
	y := 2;
	array[x + y] := 42;
	x := array[y];

END ArrayAccess64.
