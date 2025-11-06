package ch.pitchtech.modula.library.iso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.pitchtech.modula.runtime.Runtime.IRef;
import ch.pitchtech.modula.runtime.Runtime.Ref;

public class InOut {
    
    private final static InOut instance = new InOut();
    private BufferedReader consoleReader;
    
    private String lineRemaining = null;
    public boolean Done;
    
    
    private InOut() {
        
    }
    
    public boolean isDone() {
        return Done;
    }
    
    public void setDone(boolean done) {
        Done = done;
    }

    public static InOut instance() {
        return instance;
    }
    
    public void begin() {
        
    }
    
    BufferedReader reader() {
        if (consoleReader == null) {
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return consoleReader;
    }
    
    public void Read(IRef<Character> ch) {
        System.out.flush();
        try {
            int data = reader().read();
            if (data < 0) {
                ch.set((char) 0);
                Done = false;
            } else {
                ch.set((char) data);
                Done = true;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void ReadString(IRef<String> s) {
        if (lineRemaining != null) {
            String line = lineRemaining;
            int sepPos = line.indexOf(' ');
            if (sepPos > 0) {
                lineRemaining = line.substring(sepPos + 1);
                line = line.substring(0, sepPos);
            } else {
                lineRemaining = null;
            }
            s.set(line);
            Done = true;
            return;
        }
        
        System.out.flush();
        try {
            String line = reader().readLine();
            if (line == null) {
                s.set("");
                Done = false;
            } else {
                int sepPos = line.indexOf(' ');
                if (sepPos > 0) {
                    lineRemaining = line.substring(sepPos + 1);
                    line = line.substring(0, sepPos);
                }
                s.set(line);
                Done = true;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void ReadString(char[] s) {
        IRef<String> strRef = new Ref<>();
        ReadString(strRef);
        String str = strRef.get();
        for (int i = 0; i < s.length; i++) {
            if (i >= str.length()) {
                s[i] = (char) 0;
                break;
            }
            s[i] = str.charAt(i);
        }
    }
    
    public void WriteString(String s) {
        System.out.print(s);
    }
    
    public void WriteString(char[] s) {
        WriteString(new String(s));
    }
    
    public void Write(char ch) {
        System.out.print(ch);
    }
    
    public void WriteBf() {
        System.out.flush();
    }
    
    public void WriteLn() {
        System.out.println();
    }
    
    public void WriteInt(int value, long n) {
        String str = "" + value;
        if (str.length() < n) {
            StringBuilder prefix = new StringBuilder();
            for (int k = 0; k < n - str.length(); k++)
                prefix.append(' ');
            str = prefix.toString() + str;
        }
        System.out.print(str);
    }
    
    public void WriteCard(int value, long n) {
        String str = "" + Integer.toUnsignedLong(value);
        if (str.length() < n) {
            StringBuilder prefix = new StringBuilder();
            for (int k = 0; k < n - str.length(); k++)
                prefix.append(' ');
            str = prefix.toString() + str;
        }
        System.out.print(str);
    }
    
    public void ReadLongInt(IRef<Long> n) {
        IRef<String> line = new Ref<>();
        ReadString(line);
        if (!Done)
            return;
        try {
            long value = Long.parseLong(line.get());
            n.set(value);
        } catch (NumberFormatException ex) {
            Done = false;
        }
    }
    
    public void close() {
        
    }

}
