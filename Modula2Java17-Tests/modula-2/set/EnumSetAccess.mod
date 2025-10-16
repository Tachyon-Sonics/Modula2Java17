MODULE EnumSetAccess;

	TYPE
		MyEnum = (One, Two, Three, Sun);
		MySet = SET OF MyEnum;

	VAR
		set: MySet;
		index: MyEnum;
		test: BOOLEAN;
		
BEGIN

	index := One;
	INCL(set, index);
	index := Two;
	EXCL(set, index);
	index := Sun;
	test := (index IN set);

END EnumSetAccess.