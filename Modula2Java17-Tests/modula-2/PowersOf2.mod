MODULE PowersOf2;

FROM   InOut	 IMPORT	Write, WriteLn, WriteBf, WriteString, WriteInt;

CONST  M = 11;
       N = 32;

VAR    i, j, k, exp,
       carry, rest, t		: INTEGER;
       d     			: ARRAY [0..M] OF INTEGER;
       f			: ARRAY [0..N] OF INTEGER;

BEGIN
  d[0] := 1;
  k    := 1;
  FOR  exp := 1  TO  N  DO
    carry := 0;
    FOR  i := 0  TO  k - 1  DO
      t := 2 * d [i] + carry;
      IF  t >= 10  THEN
        d [i] := t - 10;
	carry := 1
      ELSE
        d [i] := t;
	carry := 0
      END      
    END;
    IF  carry > 0  THEN
      d [k] := 1;
      INC (k)
    END;
    i := M;
    REPEAT
      DEC (i);
      Write (" ")
    UNTIL  i = k;
    REPEAT
      DEC (i);
      Write (CHR (d [i] + ORD ("0")))
    UNTIL  i = 0;
    WriteInt (exp, 5);
    WriteString ("  0.");
    rest := 0;
    FOR  j := 1  TO  exp - 1  DO
      rest := 10 * rest + f [j];
      f [j] := rest DIV 2;
      rest := rest MOD 2;
      Write (CHR (f [j] + ORD ("0")))
    END;
    f [exp] := 5;
    Write ("5");
    WriteLn;
    WriteBf
  END
END PowersOf2.