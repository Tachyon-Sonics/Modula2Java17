package ch.pitchtech.modula.converter;

import java.io.IOException;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CompileChaosCastle {

    private final static String SOURCE_DIRECTORY = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/ChaosCastle/src";

    
    public static void main(String[] args) throws IOException {
        Path chaosCastleMode = Path.of(SOURCE_DIRECTORY, "ChaosCastle.mod");
        SourceFile chaosCastle = new SourceFile(chaosCastleMode);
        
        FileOptions fileOptions = new FileOptions(
                Path.of(SOURCE_DIRECTORY),
                Path.of(TARGET_DIRECTORY_MAIN), 
                Path.of(TARGET_DIRECTORY_LIBRARY));
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        compiler.compile(chaosCastle);
    }
    
}
