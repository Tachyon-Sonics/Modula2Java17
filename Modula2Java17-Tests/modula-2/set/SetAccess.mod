MODULE SetAccess;

	VAR
		zeSet: SET OF [0..31];
		index: CARDINAL;
		test: BOOLEAN;
		
BEGIN

	index := 4;
	INCL(zeSet, index);
	index := 6;
	EXCL(zeSet, index);
	index := 5;
	test := (index IN zeSet);

END SetAccess.