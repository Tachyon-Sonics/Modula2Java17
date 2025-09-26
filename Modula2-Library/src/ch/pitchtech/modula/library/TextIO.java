package ch.pitchtech.modula.library;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.filechooser.FileSystemView;

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
    
    private static class InputFileImpl implements File {
        
        public InputFileImpl(Reader reader) {
            this.reader = reader;
        }

        final Reader reader;
        
        private int lastChar;
        private Integer readAgain;
        
        
        public int read() {
            if (readAgain != null) {
                lastChar = readAgain;
                readAgain = null;
                return lastChar;
            }
            try {
                lastChar = reader.read();
                return lastChar;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        public void readAgain() {
            readAgain = lastChar;
        }
        
    }

    // Impl

    private InputStream lookupStream(String fileName) {
        return TextIO.class.getResourceAsStream("/" + fileName);
    }
    
    private Path lookupPath(String fileName) {
        String appName = Runtime.getAppNameOrDefault();
        java.io.File baseDir = FileSystemView.getFileSystemView().getDefaultDirectory();
        java.io.File appDir = new java.io.File(baseDir, appName);
        appDir.mkdir();
        java.io.File binDir = new java.io.File(appDir, ".data");
        binDir.mkdir();
        return binDir.toPath().resolve(fileName);
    }

    // PROCEDURE

    public void OpenInput(/* VAR */ Runtime.IRef<File> file, String name) {
        // Try resource
        InputStream input = lookupStream(name);
        if (input != null) {
            Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
            InputFileImpl fileImpl = new InputFileImpl(reader);
            file.set(fileImpl);
            return;
        }
        // Try file
        Path path = lookupPath(name);
        if (Files.isReadable(path)) {
            try {
                input = Files.newInputStream(path);
                Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8);
                InputFileImpl fileImpl = new InputFileImpl(reader);
                file.set(fileImpl);
                return;
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }
        file.set(null);
    }

    public void OpenOutput(/* VAR */ Runtime.IRef<File> file, String name) {
        // todo implement OpenOutput
        throw new UnsupportedOperationException("Not implemented: OpenOutput");
    }

    public void Close(File file) {
        InputFileImpl f = (InputFileImpl) file;
        try {
            f.reader.close();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void GetChar(File file, /* VAR */ Runtime.IRef<Character> x) {
        InputFileImpl f = (InputFileImpl) file;
        int ch = f.read();
        x.set((char) ch);
    }

    public void GetString(File file, /* VAR */ Runtime.IRef<String> x) {
        // todo implement GetString
        throw new UnsupportedOperationException("Not implemented: GetString");
    }

    public void GetCard(File file, /* VAR */ Runtime.IRef<Integer> x) {
        // todo implement GetCard
        throw new UnsupportedOperationException("Not implemented: GetCard");
    }

    public void GetInt(File file, /* VAR */ Runtime.IRef<Integer> x) {
        InputFileImpl f = (InputFileImpl) file;
        int result = 0;
        int ch = f.read();
        while (ch >= '0' && ch <= '9') {
            result = (result * 10) + (ch - '0');
            ch = f.read();
        }
        if (ch < ' ')
            f.readAgain();
        x.set(result);
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
        if (name.get() == null || name.get().isBlank())
            return false;
        if (!ForWriting) {
            // Try local resource
            try (InputStream input = lookupStream(name.get())) {
                return true;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        // Try file
        Path path = lookupPath(name.get());
        if (ForWriting) {
            return Files.isWritable(path);
        } else {
            return Files.isReadable(path);
        }
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
