MODULE EscapeSeq;

IMPORT	InOut, Strings, Conversions;

TYPE	AttrMode 		= (Normal, Bright, Underlined, Blinking, Reverse, Invisible);
	Colours			= (Black, Red, Green, Yellow, Blue, Magenta, Cyan, White);

PROCEDURE CtrlSeq (seq		: ARRAY OF CHAR);

BEGIN
   InOut.Write (33C);
   InOut.WriteString (seq);
   InOut.WriteBf
END CtrlSeq;


PROCEDURE Cls;

BEGIN
   CtrlSeq ('[2J')
END Cls;


PROCEDURE Cursor (lin, col	: CARDINAL);

VAR   str, tmp		: ARRAY [0..9] OF CHAR;
      ok1, ok2		: BOOLEAN;

BEGIN
   str := '[';
   Conversions.CardToStr (lin, tmp, ok1);
   Strings.Append (tmp, str);
   Strings.Append (';', str);
   Conversions.CardToStr (col, tmp, ok2);
   Strings.Append (tmp, str);
   Strings.Append ('H', str);
   IF  ok1 AND ok2  THEN
      CtrlSeq (str)
   END
END Cursor;


PROCEDURE Attribute (attr    : AttrMode);

BEGIN
   CASE  attr  OF
     Bright	: CtrlSeq ('[1m')	|
     Underlined	: CtrlSeq ('[4m')	|
     Blinking	: CtrlSeq ('[5m')	|
     Reverse	: CtrlSeq ('[7m')	|
     Invisible	: CtrlSeq ('[8m')
   ELSE
     CtrlSeq ('[0m')
   END
END Attribute;


PROCEDURE Foreground (colour 	: Colours);

BEGIN
   CASE  colour  OF
     Black	: CtrlSeq ('[30m')	|
     Red	: CtrlSeq ('[31m')	|
     Green	: CtrlSeq ('[32m')	|
     Yellow	: CtrlSeq ('[33m')	|
     Blue	: CtrlSeq ('[34m')	|
     Magenta	: CtrlSeq ('[35m')	|
     Cyan	: CtrlSeq ('[36m')
   ELSE
     CtrlSeq ('[37m')
   END
END Foreground;


PROCEDURE Background (colour 	: Colours);

BEGIN
   CASE  colour  OF
     Black	: CtrlSeq ('[40m')	|
     Red	: CtrlSeq ('[41m')	|
     Green	: CtrlSeq ('[42m')	|
     Yellow	: CtrlSeq ('[43m')	|
     Blue	: CtrlSeq ('[44m')	|
     Magenta	: CtrlSeq ('[45m')	|
     Cyan	: CtrlSeq ('[46m')
   ELSE
     CtrlSeq ('[47m')
   END
END Background;


BEGIN
   Cls;
   Cursor (0, 0);

   Background (Red);		InOut.Write (' ');
   Background (Black);		Attribute (Bright);		Foreground (Yellow);
   InOut.WriteString ('                                       ');
   Attribute (Normal);		Background (Red);		Foreground (Cyan);
   InOut.Write (' ');

   InOut.WriteString ("This is line 1");	InOut.WriteLn;		InOut.WriteBf;
   Foreground (Red);
   Background (Black);
   InOut.WriteString ("This is line 2");	InOut.WriteLn;		InOut.WriteBf;
   Foreground (Green);
   InOut.WriteString ("This is line 3");	InOut.WriteLn;		InOut.WriteBf;
   Foreground (Yellow);
   Attribute (Bright);
   InOut.WriteString ("This is line 4");	InOut.WriteLn;		InOut.WriteBf;
   Foreground (White);
   Background (Black);
   Attribute (Normal);
END EscapeSeq.