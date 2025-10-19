MODULE Card8Test;

FROM  InOut  IMPORT  WriteCard, WriteLn;


	VAR
		sc1, sc2, sc3: SHORTCARD;

BEGIN

	sc1 := 255;
	sc2 := 17;
	sc3 := sc1 / sc2;
	InOut.WriteCard(sc3, 2);
	InOut.WriteLn();

END Card8Test.