package ch.pitchtech.modula.converter.antlr;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.antlr.v4.Tool;

/**
 * Regenerate Java classes and resources of the <tt>ch.pitchtech.modula.converter.antlr.m2</tt> package.
 * Use after modifying the grammar <tt>m2pim4.g4</tt>.
 */
public class BuildJavaCodeFromGrammar {
    
    public static void main(String[] args) {
        Path projectDir = new File("").toPath();
        Path srcDir = projectDir.resolve("src");
        Path packageDir = srcDir.resolve("ch").resolve("pitchtech").resolve("modula").resolve("converter").resolve("antlr").resolve("m2");
        Path grammarFile = packageDir.resolve("m2pim4.g4");
        
        List<String> argList = List.of(
                "-package", "ch.pitchtech.modula.converter.antlr.m2",
                grammarFile.toString());
        Tool.main(argList.toArray(String[]::new));
    }

}
