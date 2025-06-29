package ch.pitchtech.modula2.converter.tool;

import java.io.IOException;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;
import ch.pitchtech.modula.converter.utils.Logger;

public class CompileMiscTestProgs {

    private final static String SOURCE_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String SOURCE_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/modula2";
    
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/src";


    /**
     * Compile GraphTest.mod and DialogTest.mod
     */
    public static void main(String[] args) throws IOException {
        FileOptions fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_MAIN));
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_LIBRARY));
        fileOptions.setTargetMainDir(Path.of(TARGET_DIRECTORY_MAIN));
        fileOptions.setTargetLibraryDir(Path.of(TARGET_DIRECTORY_LIBRARY));
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        
        {
            Compiler compiler = new Compiler(fileOptions, compilerOptions);
            Path mainModulePath = Path.of(SOURCE_DIRECTORY_MAIN, "GraphTest.mod");
            SourceFile mainModule = new SourceFile(mainModulePath);
            compiler.compile(mainModule);
        }
        {
            Logger.setVerboseLevel(2);
            Compiler compiler = new Compiler(fileOptions, compilerOptions);
            Path mainModulePath = Path.of(SOURCE_DIRECTORY_MAIN, "DialogTest.mod");
            SourceFile mainModule = new SourceFile(mainModulePath);
            compiler.compile(mainModule);
        }
    }
    
}
