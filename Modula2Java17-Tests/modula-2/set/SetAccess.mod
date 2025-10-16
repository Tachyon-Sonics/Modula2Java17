MODULE SetAccess;

	VAR
		zeSet, set2, set3: SET OF [0..31];
		index: CARDINAL;
		test: BOOLEAN;
		
BEGIN

	index := 4;
	INCL(zeSet, index);
	index := 6;
	EXCL(zeSet, index);
	index := 5;
	test := (index IN zeSet);
	
	zeSet := set2 + set3;
	zeSet := set2 - set3;
	zeSet := set2 * set3;
	zeSet := set2 / set3;

END SetAccess.