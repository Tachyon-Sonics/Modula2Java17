package ch.pitchtech.modula2.converter.tool;

import java.io.IOException;
import java.nio.file.Path;

import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CompileChaosCastle {

    private final static String SOURCE_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String SOURCE_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/modula2";
    
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/src";

    
    /**
     * Compile the "ChaosCastle" project. Modula-2 source are expected to be in the
     * "modula2" folder of the "ChaosCastle" Eclipse project. Generated Java source 
     * are put in the "src" folder of the same project, overwriting any existing files.
     * <p>
     * Library .def files are assumed to be in the "modula2" folder of the "Library"
     * Eclipse project. Skeleton Java files are placed in the "src" folder of the same
     * project, but only if they do not already exist.
     */
    public static void main(String[] args) throws IOException {
        Path chaosCastleMod = Path.of(SOURCE_DIRECTORY_MAIN, "ChaosCastle.mod");
        SourceFile chaosCastle = new SourceFile(chaosCastleMod);
        
        FileOptions fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_MAIN));
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_LIBRARY));
        fileOptions.setTargetMainDir(Path.of(TARGET_DIRECTORY_MAIN));
        fileOptions.setTargetLibraryDir(Path.of(TARGET_DIRECTORY_LIBRARY));
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        compiler.compile(chaosCastle);
    }
    
}
