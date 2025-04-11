package ch.pitchtech.modula.converter;

import java.io.IOException;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CompileGrotte {

    private final static String SOURCE_DIRECTORY = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/ChaosCastle/src";

    
    public static void main(String[] args) throws IOException {
        Path grotteMode = Path.of(SOURCE_DIRECTORY, "Grotte.mod");
        SourceFile grotte = new SourceFile(grotteMode);
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        Compiler compiler = new Compiler(compilerOptions);
        compiler.compile(grotte);
    }
    
}
