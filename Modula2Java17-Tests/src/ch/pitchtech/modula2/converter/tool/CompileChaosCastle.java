package ch.pitchtech.modula2.converter.tool;

import java.io.IOException;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

//Example of big Java code: https://github.com/pkclsoft/ORCA-Modula-2
//TODO (A) preserve comments and new lines inside of expressions
/*
* TODO various tests, namely:
* - examples from https://fruttenboel.nl/mhc/
* - https://github.com/michelou/m2-examples
* - https://sourceforge.net/projects/modula2chess/files/
* - https://www.modula2.org/freepages/downl.html#sour
* 
* TODO (3) Comment in generated Java files: (* Converted from Modula-2 by Modula2Java17 *)
*/
public class CompileChaosCastle {

    private final static String SOURCE_DIRECTORY = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/src";

    
    public static void main(String[] args) throws IOException {
        Path chaosCastleMod = Path.of(SOURCE_DIRECTORY, "ChaosCastle.mod");
        SourceFile chaosCastle = new SourceFile(chaosCastleMod);
        
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
