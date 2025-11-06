package ch.pitchtech.modula2.converter.tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import ch.pitchtech.modula.converter.compiler.Compiler;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
import ch.pitchtech.modula.converter.compiler.FileOptions;
import ch.pitchtech.modula.converter.compiler.SourceFile;
import ch.pitchtech.modula.converter.utils.Logger;

/**
 * Compiles https://sourceforge.net/projects/modula2chess/files/
 * (The "src" folder must be renamed "modula-2")
 */
public class CompileChess {

    private final static String SOURCE_DIRECTORY_MAIN = "../../Modula2-Examples/Modula2Chess/src";
    private final static String TARGET_DIRECTORY_MAIN = "../../Modula2-Examples/Modula2Chess/java";

    
    public static void main(String[] args) throws IOException {
        Path grotteMod = Path.of(SOURCE_DIRECTORY_MAIN, "Modula2Chess.mi");
        SourceFile grotte = new SourceFile(grotteMod);
        
        FileOptions fileOptions = new FileOptions();
        fileOptions.addM2sourceDir(Path.of(SOURCE_DIRECTORY_MAIN));
        fileOptions.setTargetMainDir(Path.of(TARGET_DIRECTORY_MAIN));
        fileOptions.setStandardLibrary("mocka");
        
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setCharset(StandardCharsets.ISO_8859_1);
        compilerOptions.setTargetPackageMain("ch.pitchtech.modula.chess");
        compilerOptions.setTargetPackageLib("ch.pitchtech.modula.library.mocka");
        compilerOptions.setDataModel(DataModelType.DM_32);
        Logger.setVerboseLevel(2);
        
        Compiler compiler = new Compiler(fileOptions, compilerOptions);
        compiler.compile(grotte);
    }
    
}
