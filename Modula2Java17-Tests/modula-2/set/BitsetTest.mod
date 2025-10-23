MODULE BitsetTest;

	VAR
		bits: BITSET;
		index: CARDINAL;
		test: BOOLEAN;

BEGIN

	index := 4;
	INCL(bits, index);
	index := 6;
	EXCL(bits, index);
	index := 5;
	test := (index IN bits);

END BitsetTest.
