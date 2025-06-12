package ch.pitchtech.modula.converter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import ch.pitchtech.modula.converter.cmd.CmdOptions;
import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;

public class CompileGrotte { // TODO (1) move all these utils to test project

    private final static String SOURCE_DIRECTORY = "../../ChaosCastle/Grotte/modula2";
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/Grotte/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/Library/src";

    
    public static void main0(String[] args) throws IOException {
        Path grotteMod = Path.of(SOURCE_DIRECTORY, "Grotte.mod");
        SourceFile grotte = new SourceFile(grotteMod);
        
        FileOptions fileOptions = new FileOptions(
                Path.of(SOURCE_DIRECTORY),
                Path.of(TARGET_DIRECTORY_MAIN), 
                Path.of(TARGET_DIRECTORY_LIBRARY));
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain("ch.chaos.castle");
        compilerOptions.setTargetPackageLib("ch.chaos.library");
        
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        compiler.compile(grotte);
    }
    
    public static void main(String[] unused) throws IOException {
        Path grotteMod = Path.of(SOURCE_DIRECTORY, "Grotte.mod");
        List<String> args = List.of(
                grotteMod.toString(),
                "-" + CmdOptions.TARGET_MAIN_DIR.getName(), Path.of(TARGET_DIRECTORY_MAIN).toString(),
                "-" + CmdOptions.TARGET_LIBRARY_DIR.getName(), Path.of(TARGET_DIRECTORY_LIBRARY).toString(),
                "-" + CmdOptions.TARGET_PACKAGE_MAIN.getName(), "ch.chaos.grotte",
                "-" + CmdOptions.TARGET_PACKAGE_LIB.getName(), "ch.chaos.library"
                );
        Modula2JavaTranslator.main(args.toArray(String[]::new));
    }
    
}
