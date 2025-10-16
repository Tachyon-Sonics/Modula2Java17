MODULE SetAccess64;

	TYPE
		MySet = SET OF [0..63];

	VAR
		set, set2, set3: MySet;
		index: CARDINAL;
		test: BOOLEAN;
		
BEGIN

	index := 4;
	INCL(set, index);
	index := 6;
	EXCL(set, index);
	index := 5;
	test := (index IN set);
	
	set := set2 + set3;
	set := set2 - set3;
	set := set2 * set3;
	set := set2 / set3;

END SetAccess64.