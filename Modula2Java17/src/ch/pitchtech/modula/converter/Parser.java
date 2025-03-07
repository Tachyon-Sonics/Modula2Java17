package ch.pitchtech.modula.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Lexer;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CompilationUnitContext;

public class Parser {

    public static void main(String[] args) throws IOException {
        System.out.println("Antlr4 Example");
        Path resultFile = Path.of("result.txt");
        Files.deleteIfExists(resultFile);
        try {
            File dir = new File("modula2");
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".def") 
                        || ((file.getName().startsWith("Chaos") || file.getName().startsWith("Grotte")) 
                                && file.getName().endsWith(".mod"))) {
                    System.out.flush();
                    System.err.flush();
                    System.out.println("*** " + file.getName() + " ***");
                    String content = Files.readString(file.toPath());
                    
                    /*
                     * ARRAY[0..5] parses "0." as a real number and then fails to see "..";
                     * => transform to ARRAY[0 ..5]
                     */
                    content = content.replaceAll("ARRAY\\[(\\d+)\\.\\.", "ARRAY[$1 ..");
                    
                    // {0..5} => {0 ..5} (see above)
                    content = content.replaceAll("\\{(\\d+)\\.\\.(\\d+)\\}", "{$1 ..$2}");
                    
                    // Remove register definitions (M2Amiga)
                    content = content.replaceAll("\\{R\\.\\w\\w\\}", "");
                    InputStream inputStream = new ByteArrayInputStream(content.getBytes());
                    /*
                     * make Lexer
                     */
                    Lexer lexer = new m2pim4Lexer(CharStreams.fromStream(inputStream));
                    /*
                     * get a TokenStream on the Lexer
                     */
                    TokenStream tokenStream = new CommonTokenStream(lexer);
                    /*
                     * make a Parser on the token stream
                     */
                    m2pim4Parser parser = new m2pim4Parser(tokenStream);
                    /*
                     * get the top node of the AST. This corresponds to the topmost rule of equation.q4, "equation"
                     */
                    CompilationUnitContext module = parser.compilationUnit();
                    String tree = module.toStringTree() + "\n\n";
                    Files.writeString(resultFile, tree, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                 }
            }
       } catch (IOException e) {
            e.printStackTrace();
        }
    }
}