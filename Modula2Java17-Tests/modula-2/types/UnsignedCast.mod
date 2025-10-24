MODULE UnsignedCast;

IMPORT InOut;

	VAR
		arr: ARRAY [0..255] OF INTEGER;
		index: SHORTCARD;
		index2: CARDINAL;
		index3: LONGCARD;
		value: INTEGER;
		big: LONGINT;
		
BEGIN

	arr[255] := 42;
	index := 255;
	
	(* SHORTCARD array access *)
	value := arr[index];
	InOut.WriteInt(value, 2);
	InOut.WriteLn();
	
	(* Upcast to CARDINAL and LONGCARD *)
	index2 := index;
	index3 := index;
	index3 := index2;
	
	(* CARDINAL array access *)
	value := arr[index2];
	InOut.WriteInt(value, 2);
	InOut.WriteLn();
	
	(* Upcast to INTEGER *)
	value := index;
	value := index2;
	value := index3;

	(* Upcast to LONGINT *)
	big := index;
	big := index2;
	big := index3;

END UnsignedCast.