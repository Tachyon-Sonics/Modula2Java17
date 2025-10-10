package ch.pitchtech.modula2.converter.tool;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import ch.pitchtech.modula.converter.Modula2JavaTranslator;
import ch.pitchtech.modula.converter.cmd.CmdOptions;
import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CompileGrotte {

    private final static String SOURCE_DIRECTORY_MAIN = "../../ChaosCastle/Grotte/modula2";
    private final static String SOURCE_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/modula2";
    
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/Grotte/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/src";

    
    /**
     * Compile the "Grotte" Modula-2 project using a {@link Compiler} instance
     */
    public static void main0(String[] args) throws IOException {
        Path grotteMod = Path.of(SOURCE_DIRECTORY_MAIN, "Grotte.mod");
        SourceFile grotte = new SourceFile(grotteMod);
        
        FileOptions fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_MAIN));
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_LIBRARY));
        fileOptions.setTargetMainDir(Path.of(TARGET_DIRECTORY_MAIN));
        fileOptions.setTargetLibraryDir(Path.of(TARGET_DIRECTORY_LIBRARY));
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        compiler.compile(grotte);
    }
    
    /**
     * Compile the "Grotte" Modula-2 project by simulating a command-line
     * (calling {@link Modula2JavaTranslator#main(String[])} with command-line args)
     */
    public static void main(String[] unused) throws IOException {
        Path grotteMod = Path.of(SOURCE_DIRECTORY_MAIN, "Grotte.mod");
        List<String> args = List.of(
                grotteMod.toString(),
                "-" + CmdOptions.SOURCE_DIR.getName(), Path.of(SOURCE_DIRECTORY_MAIN).toString(),
                "-" + CmdOptions.SOURCE_DIR.getName(), Path.of(SOURCE_DIRECTORY_LIBRARY).toString(),
                "-" + CmdOptions.TARGET_MAIN_DIR.getName(), Path.of(TARGET_DIRECTORY_MAIN).toString(),
                "-" + CmdOptions.TARGET_LIBRARY_DIR.getName(), Path.of(TARGET_DIRECTORY_LIBRARY).toString(),
                "-" + CmdOptions.TARGET_PACKAGE_MAIN.getName(), "ch.chaos.grotte",
                "-" + CmdOptions.TARGET_PACKAGE_LIB.getName(), "ch.chaos.library",
                "-" + CmdOptions.DATA_MODEL.getName(), DataModelType.LOOSE_16_32.getArgName()
                );
        Modula2JavaTranslator.main(args.toArray(String[]::new));
    }
    
}
