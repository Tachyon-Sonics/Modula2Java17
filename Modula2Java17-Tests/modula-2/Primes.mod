MODULE Primes;

FROM   InOut  	IMPORT  WriteLn, WriteInt;

CONST  N  = 500;
       M  =  23;
       LL =  10;

VAR    i, k, x, inc, lim, square, L	: INTEGER;
       prime 	     	  	  	: BOOLEAN;
       P, V				: ARRAY [0..M] OF INTEGER;

BEGIN
  L := 0;
  x := 1;
  inc := 4;
  lim := 1;
  square := 9;
  FOR  i := 3  TO  N  DO
    REPEAT
      x := x + inc;
      inc := 6 - inc;
      IF  square <= x  THEN
        INC (lim);
	V [lim] := square;
	square := P [lim + 1] * P [lim + 1];
      END;
      k := 2;
      prime := TRUE;
      WHILE  prime AND (k < lim)  DO
        INC (k);
	IF  V [k] < x  THEN  V [k] := V [k] + 2 * P [k]  END;
	prime := x # V [k]
      END
    UNTIL  prime;
    IF  i < M  THEN  P [i] := x  END;
    WriteInt (x, 6);
    INC (L);
    IF  L = LL  THEN
      WriteLn;  
      L := 0  
    END
  END;
  WriteLn
END Primes.