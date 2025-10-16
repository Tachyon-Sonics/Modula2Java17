MODULE EnumSetAccess;

	TYPE
		MyEnum = (One, Two, Three, Sun);
		MySet = SET OF MyEnum;

	VAR
		set, set2, set3: MySet;
		index: MyEnum;
		test: BOOLEAN;
		
BEGIN

	index := One;
	INCL(set, index);
	index := Two;
	EXCL(set, index);
	index := Sun;
	test := (index IN set);
	
	set := set2 + set3;
	set := set2 - set3;
	set := set2 * set3;
	set := set2 / set3;

END EnumSetAccess.