MODULE UnsignedTest;

	VAR
		sc1, sc2, sc3: SHORTCARD;
		c1, c2, c3: CARDINAL;
		lc1, lc2, lc3: LONGCARD;
		t: BOOLEAN;
		
BEGIN

	sc1 := 20;
	sc2 := 6;
	sc3 := sc1 + sc2;
	sc3 := sc1 - sc2;
	sc3 := sc1 * sc2;
	sc3 := sc1 / sc2;
	sc3 := sc1 MOD sc2;
	t := (sc1 < sc2);
	t := (sc1 <= sc2);
	t := (sc1 > sc2);
	t := (sc1 >= sc2);
	t := (sc1 = sc2);

	c1 := 20;
	c2 := 6;
	c3 := c1 + c2;
	c3 := c1 - c2;
	c3 := c1 * c2;
	c3 := c1 / c2;
	c3 := c1 MOD c2;
	t := (c1 < c2);
	t := (c1 <= c2);
	t := (c1 > c2);
	t := (c1 >= c2);
	t := (c1 = c2);

	lc1 := 20;
	lc2 := 6;
	lc3 := lc1 + lc2;
	lc3 := lc1 - lc2;
	lc3 := lc1 * lc2;
	lc3 := lc1 / lc2;
	lc3 := lc1 MOD lc2;
	t := (lc1 < lc2);
	t := (lc1 <= lc2);
	t := (lc1 > lc2);
	t := (lc1 >= lc2);
	t := (lc1 = lc2);

END UnsignedTest.