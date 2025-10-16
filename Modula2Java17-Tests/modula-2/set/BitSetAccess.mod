MODULE BitSetAccess;

	VAR
		zeSet: BITSET;
		index: CARDINAL;
		test: BOOLEAN;
		
BEGIN

	index := 4;
	INCL(zeSet, index);
	index := 6;
	EXCL(zeSet, index);
	index := 5;
	test := (index IN zeSet);

END BitSetAccess.