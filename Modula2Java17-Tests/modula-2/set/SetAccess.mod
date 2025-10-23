MODULE SetAccess;

	VAR
		zeSet: SET OF [0..31];
		set1, set2, set3: SET OF [0..31];
		index: CARDINAL;
		test: BOOLEAN;

BEGIN

	index := 4;
	INCL(zeSet, index);
	index := 6;
	EXCL(zeSet, index);
	index := 5;
	test := (index IN zeSet);

	(* Test set arithmetic operators *)
	INCL(set1, 1);
	INCL(set1, 3);
	INCL(set1, 5);

	INCL(set2, 3);
	INCL(set2, 5);
	INCL(set2, 7);

	(* Union: set3 should contain {1, 3, 5, 7} *)
	set3 := set1 + set2;

	(* Difference: set3 should contain {1} *)
	set3 := set1 - set2;

	(* Intersection: set3 should contain {3, 5} *)
	set3 := set1 * set2;

	(* Symmetric difference: set3 should contain {1, 7} *)
	set3 := set1 / set2;

END SetAccess.