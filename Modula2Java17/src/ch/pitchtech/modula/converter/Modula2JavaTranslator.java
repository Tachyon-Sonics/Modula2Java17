package ch.pitchtech.modula.converter;

import java.io.IOException;

import ch.pitchtech.modula.converter.cmd.CommandLine;
import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.SourceFile;

/**
 * Modula-2 to Java 17 translator, main class.
 */
public class Modula2JavaTranslator {
    
    public static void main(String[] args) throws IOException {
        // Parse command line
        CommandLine commandLine = new CommandLine();
        String error = commandLine.parse(args);
        if (error != null) {
            System.err.println(error);
            System.exit(1);
        }
        
        // Compile to Java sources
        Compiler compiler = new Compiler(commandLine.getFileOptions(), commandLine.getCompilerOptions());
        compiler.compile(commandLine.getFilesToCompile().toArray(SourceFile[]::new));
    }

}
