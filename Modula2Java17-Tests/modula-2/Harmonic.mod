MODULE Harmonic;

FROM  InOut		IMPORT  ReadLongInt, Done, Write, WriteString, WriteBf, WriteLn;
FROM  RealInOut		IMPORT	WriteLongReal;

VAR   i, n		: LONGINT;
      x, d, s1, s2	: LONGREAL;

BEGIN
  WriteString ("n = ");		WriteBf;	ReadLongInt (n);
  WHILE  Done  DO
    s1 := 0.0;
    d  := 0.0;
    i  := 0;
    REPEAT
      d := d + 1.0;
      s1 := s1 + 1.0 / d;
      INC (i)
    UNTIL  i >= n;
    WriteLongReal (s1, 16);		Write (11C);
    s2 := 0.0;
    REPEAT
      s2 := s2 + 1.0 / d;
      d := d - 1.0;
      DEC (i)
    UNTIL  i = 0;
    WriteLongReal (s2, 16);		Write (11C);
    
    WriteString ("  Diff = ");	WriteLongReal (100.0 * (s2 - s1) / s1, 16);
    Write ("%");      	 	WriteLn;
    WriteString ("n = ");	WriteBf;	ReadLongInt (n);
  END;
  WriteLn
END Harmonic.