package ch.pitchtech.modula.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.pitchtech.modula.runtime.Runtime.IRef;
import ch.pitchtech.modula.runtime.Runtime.Ref;

public class InOut {
    
    private final static InOut instance = new InOut();
    private BufferedReader consoleReader;
    
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
    
    private BufferedReader reader() {
        if (consoleReader == null) {
            consoleReader = new BufferedReader(new InputStreamReader(System.in));
        }
        return consoleReader;
    }
    
    public void ReadChar(IRef<Character> ch) {
        System.out.flush();
        try {
            int data = reader().read();
            if (data < 0) {
                ch.set((char) 0);
                Done = true;
            } else {
                ch.set((char) data);
                Done = false;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void ReadString(IRef<String> s) {
        System.out.flush();
        try {
            String line = reader().readLine();
            if (line == null) {
                s.set("");
                Done = true;
            } else {
                s.set(line);
                Done = false;
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
    
    public void close() {
        
    }

}
