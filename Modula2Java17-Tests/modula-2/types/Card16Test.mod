MODULE Card16Test;

FROM  InOut  IMPORT  WriteCard, WriteLn;


	VAR
		sc1, sc2, sc3: CARDINAL;

BEGIN

	sc1 := 65535;
	sc2 := 255;
	sc3 := sc1 / sc2;
	InOut.WriteCard(sc3, 3);
	InOut.WriteLn();

END Card16Test.