package ch.pitchtech.modula.converter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Lexer;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CompilationUnitContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionModuleContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProgramModuleContext;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.generator.ImplementationModuleGenerator;
import ch.pitchtech.modula.converter.generator.ModuleGenerator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.StubImplementationGenerator;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.source.CurrentFile;
import ch.pitchtech.modula.converter.parser.DefinitionModuleProcessor;
import ch.pitchtech.modula.converter.parser.ImplementationModuleProcessor;
import ch.pitchtech.modula.converter.parser.ModuleProcessor;
import ch.pitchtech.modula.converter.transform.Transforms;

// Example of big Java code: https://github.com/pkclsoft/ORCA-Modula-2
// TODO (A) preserve comments and new lines inside of expressions
/*
 * TODO various tests, namely:
 * - examples from https://fruttenboel.nl/mhc/
 * - https://github.com/michelou/m2-examples
 * - https://sourceforge.net/projects/modula2chess/files/
 * - https://www.modula2.org/freepages/downl.html#sour
 * 
 * TODO (1) NEW, DISPOSE, ALLOCATE, DEALLOCATE
 * TODO (3) Comment in generated Java files: (* Converted from Modula-2 by Modula2Java17 *)
 */
public class CompileAllLegacy {
    
    private final static String SOURCE_DIRECTORY = "../../ChaosCastle/ChaosCastle/modula2";
    private final static String TARGET_DIRECTORY_MAIN = "../../ChaosCastle/ChaosCastle/src";
    private final static String TARGET_DIRECTORY_LIBRARY = "../../ChaosCastle/ChaosCastle/src";
    
    private final static String TARGET_PACKAGE = "ch.chaos.castle";
    private final static String TARGET_PACKAGE_LIBRARY = "ch.chaos.library";
  
    private final static boolean ALWAYS_OVERRIDE_STUB = false;
    private final static boolean DUMP_ABSTRACT_TREE = false;
            
    
    private final Application application;
    private Map<ICompilationUnit, Path> filesToProcess = new LinkedHashMap<>(); // Path is source path
    
    
    private CompileAllLegacy() {
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setTargetPackageMain(TARGET_PACKAGE);
        compilerOptions.setTargetPackageLib(TARGET_PACKAGE_LIBRARY);
        compilerOptions.setAlwaysOverrideStubs(false);
        application = new Application(compilerOptions);
    }
    
    public void parse(Path m2sourceFile) throws IOException {
        System.out.println("Parsing " + m2sourceFile.toAbsolutePath());
        
        CurrentFile.setCurrentFile(m2sourceFile);
        String content = Files.readString(m2sourceFile);
        
        // Step 1: pre-processing
        content = preProcess(content);
        
        // Step 2: lexer + parser
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        Lexer lexer = new m2pim4Lexer(CharStreams.fromStream(inputStream));
        TokenStream tokenStream = new CommonTokenStream(lexer);
        m2pim4Parser parser = new m2pim4Parser(tokenStream);
        
        CompilationUnitContext cuContext = parser.compilationUnit();
        
        // Setp 3: abstraction into a model
        ICompilationUnit compilationUnit = createCompilationUnit(cuContext);
        
        if (DUMP_ABSTRACT_TREE) {
            System.out.println(compilationUnit.toTreeString());
        }

        // Step 4: analyses + transforms
        Transforms.applyTransforms(compilationUnit, application.getCompilerOptions());
        
        filesToProcess.put(compilationUnit, m2sourceFile);
    }
    
    private void generateCode(Path targetPackageDir, Path targetLibraryDir) throws IOException {
        for (Map.Entry<ICompilationUnit, Path> entry : filesToProcess.entrySet()) {
            ICompilationUnit compilationUnit = entry.getKey();
            Path m2sourceFile = entry.getValue();
            System.out.println("Generating " + m2sourceFile);
            CurrentFile.setCurrentFile(m2sourceFile);
            
            // Step 5: code generation
            ResultContext result = new ResultContext(application.getCompilerOptions());
            
            if (compilationUnit instanceof DefinitionModule definitionModule) {
                Path m2implFile = m2sourceFile.resolveSibling(m2sourceFile.getFileName().toString().replace(".def", ".mod"));
                if (!Files.isRegularFile(m2implFile)) {
                    // .def file (DEFINITION) has no corresponding .mod file (IMPLEMENTATION)
                    Path javaImplFile = targetLibraryDir.resolve(definitionModule.getName() + ".java");
                    if (!Files.isRegularFile(javaImplFile) || ALWAYS_OVERRIDE_STUB) {
                        // Stub implementation does not exist. Create it
                        ResultContext stubImpl = new ResultContext(application.getCompilerOptions());
                        new StubImplementationGenerator(definitionModule).generate(stubImpl);
                        Files.writeString(javaImplFile, stubImpl.toString());
                    }
                }
            } else if (compilationUnit instanceof ImplementationModule implementationModule) {
                new ImplementationModuleGenerator(implementationModule).generate(result);
                Path javaImplFile = targetPackageDir.resolve(implementationModule.getName() + ".java");
                Files.writeString(javaImplFile, result.toString());
            } else if (compilationUnit instanceof ch.pitchtech.modula.converter.model.Module module) {
                new ModuleGenerator(module).generate(result);
                Path javaImplFile = targetPackageDir.resolve(module.getName() + ".java");
                Files.writeString(javaImplFile, result.toString());
            }
        }
    }
    
    private String preProcess(String content) {
        /*
         * ARRAY[0..5] parses "0." as a real number and then fails to see "..";
         * => transform to ARRAY[0 ..5]
         */
        content = content.replaceAll("\\[(\\d+)\\.\\.", "[$1 ..");
        
        // {0..5} => {0 ..5} (see above)
        content = content.replaceAll("\\{(\\d+)\\.\\.(\\d+)\\}", "{$1 ..$2}");
        
        // Remove register definitions (M2Amiga)
        content = content.replaceAll("\\{R\\.\\w\\w\\}", "");
        
        return content;
    }
    
    private ICompilationUnit createCompilationUnit(CompilationUnitContext module) {
        if (module.definitionModule() != null) {
            DefinitionModuleContext definitionModule = module.definitionModule();
            DefinitionModuleProcessor processor = new DefinitionModuleProcessor(application);
            return processor.process(definitionModule);
        } else if (module.IMPLEMENTATION() != null) {
            ProgramModuleContext programModule = module.programModule();
            ImplementationModuleProcessor processor = new ImplementationModuleProcessor(application);
            return processor.process(programModule);
        } else {
            ProgramModuleContext programModule = module.programModule();
            ModuleProcessor processor = new ModuleProcessor(application);
            return processor.process(programModule);
        }
    }
    
    public static void main(String[] args) throws IOException {
        CompileAllLegacy translator = new CompileAllLegacy();
        Path targetDirMain = Path.of(TARGET_DIRECTORY_MAIN);
        Path targetDirLibrary = Path.of(TARGET_DIRECTORY_LIBRARY);
        Path targetPackageDir = targetDirMain.resolve(TARGET_PACKAGE.replace(".", File.separator));
        Files.createDirectories(targetPackageDir);
        Path targetLibraryDir = targetDirLibrary.resolve(TARGET_PACKAGE_LIBRARY.replace(".", File.separator));
        Files.createDirectories(targetLibraryDir);

        translator.parse(Path.of(SOURCE_DIRECTORY, "Memory.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ANSITerm.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Checks.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Clock.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Dialogs.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Files.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Graphics.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Input.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Languages.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Menus.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Registration.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Sounds.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Trigo.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Terminal.def"));
        
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBase.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Chaos1Zone.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Chaos2Zone.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosActions.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosAlien.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBonus.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBoss.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosCreator.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDead.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDObj.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDual.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosSounds.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosFire.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosGenerator.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosGraphics.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosImages.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosInterface.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosLevels.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosMachine.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosMissile.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosObjects.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosPlayer.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosScreens.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosSmartBonus.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosStone.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosWeapon.def"));

        translator.parse(Path.of(SOURCE_DIRECTORY, "Chaos1Zone.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "Chaos2Zone.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosActions.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosAlien.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBase.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBonus.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosBoss.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosCreator.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDead.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDObj.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosDual.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosFire.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosGenerator.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosGraphics.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosImages.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosInterface.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosLevels.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosMachine.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosMissile.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosObjects.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosPlayer.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosScreens.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosSmartBonus.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosSounds.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosStone.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosWeapon.mod"));

        translator.parse(Path.of(SOURCE_DIRECTORY, "ChaosCastle.mod"));
        
        
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteSupport.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteActions.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteBonus.def"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteSounds.def"));
        
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteSupport.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteActions.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteBonus.mod"));
        translator.parse(Path.of(SOURCE_DIRECTORY, "GrotteSounds.mod"));

        translator.parse(Path.of(SOURCE_DIRECTORY, "Grotte.mod"));
        
        translator.parse(Path.of(SOURCE_DIRECTORY, "GraphTest.mod"));
        
        translator.generateCode(targetPackageDir, targetLibraryDir);
    }

}
