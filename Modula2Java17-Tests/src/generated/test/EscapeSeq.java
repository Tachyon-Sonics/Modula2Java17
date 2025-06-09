package generated.test;

import ch.pitchtech.modula.library.*;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class EscapeSeq {

    // Imports
    private final Conversions conversions = Conversions.instance();
    private final InOut inOut = InOut.instance();
    private final Strings strings = Strings.instance();


    // TYPE

    private static enum AttrMode {
        Normal,
        Bright,
        Underlined,
        Blinking,
        Reverse,
        Invisible;
    }

    private static enum Colours {
        Black,
        Red,
        Green,
        Yellow,
        Blue,
        Magenta,
        Cyan,
        White;
    }


    // PROCEDURE

    private void CtrlSeq(String seq) {
        inOut.Write(((char) 033));
        inOut.WriteString(seq);
        inOut.WriteBf();
    }

    private void Cls() {
        CtrlSeq("[2J");
    }

    private void Cursor(int lin, int col) {
        // VAR
        Runtime.Ref<String> str = new Runtime.Ref<>("");
        Runtime.Ref<String> tmp = new Runtime.Ref<>("");
        Runtime.Ref<Boolean> ok1 = new Runtime.Ref<>(false);
        Runtime.Ref<Boolean> ok2 = new Runtime.Ref<>(false);

        str.set("[");
        conversions.CardToStr(lin, tmp, ok1);
        strings.Append(tmp.get(), str);
        strings.Append(";", str);
        conversions.CardToStr(col, tmp, ok2);
        strings.Append(tmp.get(), str);
        strings.Append("H", str);
        if (ok1.get() && ok2.get())
            CtrlSeq(str.get());
    }

    private void Attribute(AttrMode attr) {
        switch (attr) {
            case Bright -> CtrlSeq("[1m");
            case Underlined -> CtrlSeq("[4m");
            case Blinking -> CtrlSeq("[5m");
            case Reverse -> CtrlSeq("[7m");
            case Invisible -> CtrlSeq("[8m");
            default -> CtrlSeq("[0m");
        }
    }

    private void Foreground(Colours colour) {
        switch (colour) {
            case Black -> CtrlSeq("[30m");
            case Red -> CtrlSeq("[31m");
            case Green -> CtrlSeq("[32m");
            case Yellow -> CtrlSeq("[33m");
            case Blue -> CtrlSeq("[34m");
            case Magenta -> CtrlSeq("[35m");
            case Cyan -> CtrlSeq("[36m");
            default -> CtrlSeq("[37m");
        }
    }

    private void Background(Colours colour) {
        switch (colour) {
            case Black -> CtrlSeq("[40m");
            case Red -> CtrlSeq("[41m");
            case Green -> CtrlSeq("[42m");
            case Yellow -> CtrlSeq("[43m");
            case Blue -> CtrlSeq("[44m");
            case Magenta -> CtrlSeq("[45m");
            case Cyan -> CtrlSeq("[46m");
            default -> CtrlSeq("[47m");
        }
    }


    // Life Cycle

    private void begin() {
        InOut.instance().begin();
        Strings.instance().begin();
        Conversions.instance().begin();

        Cls();
        Cursor(0, 0);
        Background(Colours.Red);
        inOut.Write(' ');
        Background(Colours.Black);
        Attribute(AttrMode.Bright);
        Foreground(Colours.Yellow);
        inOut.WriteString("                                       ");
        Attribute(AttrMode.Normal);
        Background(Colours.Red);
        Foreground(Colours.Cyan);
        inOut.Write(' ');
        inOut.WriteString("This is line 1");
        inOut.WriteLn();
        inOut.WriteBf();
        Foreground(Colours.Red);
        Background(Colours.Black);
        inOut.WriteString("This is line 2");
        inOut.WriteLn();
        inOut.WriteBf();
        Foreground(Colours.Green);
        inOut.WriteString("This is line 3");
        inOut.WriteLn();
        inOut.WriteBf();
        Foreground(Colours.Yellow);
        Attribute(AttrMode.Bright);
        inOut.WriteString("This is line 4");
        inOut.WriteLn();
        inOut.WriteBf();
        Foreground(Colours.White);
        Background(Colours.Black);
        Attribute(AttrMode.Normal);
    }

    private void close() {
        Conversions.instance().close();
        Strings.instance().close();
        InOut.instance().close();
    }

    public static void main(String[] args) {
        Runtime.setArgs(args);
        EscapeSeq instance = new EscapeSeq();
        try {
            instance.begin();
        } catch (HaltException ex) {
            // Normal termination
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            instance.close();
        }
    }

}