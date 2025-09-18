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
    
    //Example of big Java code: https://github.com/pkclsoft/ORCA-Modula-2
    //TODO (A) preserve comments and new lines inside of expressions
    /*
     * TODO various tests, namely:
     * - examples from https://fruttenboel.nl/mhc/
     * - https://github.com/michelou/m2-examples
     * - https://sourceforge.net/projects/modula2chess/files/
     * - https://www.modula2.org/freepages/downl.html#sour
     * - https://www.modula2.org/tutor/chapter11.php
     * - https://github.com/sblendorio/gorilla-cpm?tab=readme-ov-file
     * - https://github.com/GunterMueller?tab=repositories&q=modula&type=&language=&sort=
     * 
     * TODO (3) Comment in generated Java files: (* Converted from Modula-2 by Modula2Java17 *)
     */
}
