package ch.pitchtech.modula.library;

import ch.pitchtech.modula.runtime.Runtime;


public class TextIO {

    private static TextIO instance;


    private TextIO() {
        instance = this; // Set early to handle circular dependencies
    }

    public static TextIO instance() {
        if (instance == null)
            new TextIO(); // will set 'instance'
        return instance;
    }


    // TYPE

    public static interface File { // Opaque type
    }


    // PROCEDURE

    public void OpenInput(/* VAR */ Runtime.IRef<File> file, String name) {
        // todo implement OpenInput
        throw new UnsupportedOperationException("Not implemented: OpenInput");
    }

    public void OpenOutput(/* VAR */ Runtime.IRef<File> file, String name) {
        // todo implement OpenOutput
        throw new UnsupportedOperationException("Not implemented: OpenOutput");
    }

    public void Close(File file) {
        // todo implement Close
        throw new UnsupportedOperationException("Not implemented: Close");
    }

    public void GetChar(File file, /* VAR */ Runtime.IRef<Character> x) {
        // todo implement GetChar
        throw new UnsupportedOperationException("Not implemented: GetChar");
    }

    public void GetString(File file, /* VAR */ Runtime.IRef<String> x) {
        // todo implement GetString
        throw new UnsupportedOperationException("Not implemented: GetString");
    }

    public void GetCard(File file, /* VAR */ Runtime.IRef<Integer> x) {
        // todo implement GetCard
        throw new UnsupportedOperationException("Not implemented: GetCard");
    }

    public void GetInt(File file, /* VAR */ Runtime.IRef<Short> x) {
        // todo implement GetInt
        throw new UnsupportedOperationException("Not implemented: GetInt");
    }

    public void GetReal(File file, /* VAR */ Runtime.IRef<Float> x) {
        // todo implement GetReal
        throw new UnsupportedOperationException("Not implemented: GetReal");
    }

    public void GetLongReal(File file, /* VAR */ Runtime.IRef<Double> x) {
        // todo implement GetLongReal
        throw new UnsupportedOperationException("Not implemented: GetLongReal");
    }

    public void PutChar(File file, char x) {
        // todo implement PutChar
        throw new UnsupportedOperationException("Not implemented: PutChar");
    }

    public void PutString(File file, /* VAR */ Runtime.IRef<String> x) {
        // todo implement PutString
        throw new UnsupportedOperationException("Not implemented: PutString");
    }

    public void PutString(File file, String x) {
        // todo implement PutString
        throw new UnsupportedOperationException("Not implemented: PutString");
    }

    public void PutCard(File file, int x, int n) {
        // todo implement PutCard
        throw new UnsupportedOperationException("Not implemented: PutCard");
    }

    public void PutOct(File file, int x, int n) {
        // todo implement PutOct
        throw new UnsupportedOperationException("Not implemented: PutOct");
    }

    public void PutHex(File file, int x, int n) {
        // todo implement PutHex
        throw new UnsupportedOperationException("Not implemented: PutHex");
    }

    public void PutInt(File file, short x, int n) {
        // todo implement PutInt
        throw new UnsupportedOperationException("Not implemented: PutInt");
    }

    public void PutReal(File file, float x, int n, short k) {
        // todo implement PutReal
        throw new UnsupportedOperationException("Not implemented: PutReal");
    }

    public void PutLongReal(File file, double x, int n, short k) {
        // todo implement PutLongReal
        throw new UnsupportedOperationException("Not implemented: PutLongReal");
    }

    public void PutLn(File file) {
        // todo implement PutLn
        throw new UnsupportedOperationException("Not implemented: PutLn");
    }

    public void PutBf(File file) {
        // todo implement PutBf
        throw new UnsupportedOperationException("Not implemented: PutBf");
    }

    public void UndoGetChar(File file) {
        // todo implement UndoGetChar
        throw new UnsupportedOperationException("Not implemented: UndoGetChar");
    }

    public boolean Done() {
        // todo implement Done
        throw new UnsupportedOperationException("Not implemented: Done");
    }

    public boolean Accessible(/* VAR */ Runtime.IRef<String> name, boolean ForWriting) {
        // todo implement Accessible
        throw new UnsupportedOperationException("Not implemented: Accessible");
    }

    public boolean EOF(File file) {
        // todo implement EOF
        throw new UnsupportedOperationException("Not implemented: EOF");
    }

    public void Erase(String name, /* VAR */ Runtime.IRef<Boolean> ok) {
        // todo implement Erase
        throw new UnsupportedOperationException("Not implemented: Erase");
    }


    public void begin() {

    }

    public void close() {

    }
}
