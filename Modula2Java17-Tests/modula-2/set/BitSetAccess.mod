MODULE BitSetAccess;

	VAR
		zeSet, set2, set3: BITSET;
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

END BitSetAccess.