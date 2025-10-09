MODULE SetAccess64;

	TYPE
		MySet = SET OF [0..63];

	VAR
		set: MySet;
		index: CARDINAL;
		test: BOOLEAN;
		
BEGIN

	index := 4;
	INCL(set, index);
	index := 6;
	EXCL(set, index);
	index := 5;
	test := (index IN set);

END SetAccess64.