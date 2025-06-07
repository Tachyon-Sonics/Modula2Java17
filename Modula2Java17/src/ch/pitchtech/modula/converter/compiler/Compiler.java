package ch.pitchtech.modula.converter.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4BaseListener;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Lexer;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CompilationUnitContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionModuleContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ImportListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProgramModuleContext;
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
import ch.pitchtech.modula.converter.utils.Logger;

public class Compiler {

    private final FileOptions fileOptions;
    private final CompilerOptions compilerOptions;
    private final Application application;
    private TreeSet<SourceFile> sourceFiles = new TreeSet<>();
    
    
    public Compiler(FileOptions fileOptions, CompilerOptions compilerOptions) {
        this.fileOptions = fileOptions;
        this.compilerOptions = compilerOptions;
        application = new Application(compilerOptions);
    }
    
    // TODO (4) allow non MODULE files, or detect and report an error
    public void compile(SourceFile... mainModuleFiles) throws IOException {
        Logger.log(1, "P1: Parsing...");
        // Parse
        parseRecursive(mainModuleFiles); // This fills 'sourceFiles'
        
        // Abstract into a model
        Logger.log(1, "P2: Abstraction...");
        for (SourceFile sourceFile : sourceFiles) {
            Logger.log(2, "  {0}", sourceFile.getPath().getFileName());
            ICompilationUnit cu = createCompilationUnit(sourceFile.getCuContext());
            sourceFile.setCompilationUnit(cu);
        }

        // Analyze + Transform
        Logger.log(1, "P3: Analyze & Transform...");
        for (SourceFile sourceFile : sourceFiles) {
            ICompilationUnit compilationUnit = sourceFile.getCompilationUnit();
            Logger.log(2, "  {0}", compilationUnit.getName());
            Transforms.applyTransforms(compilationUnit, compilerOptions);
        }
        
        // Generate Java code
        Logger.log(1, "P4: Generate code...");
        Path targetDirMain = fileOptions.getTargetMainDir();
        Path targetDirLibrary = fileOptions.getTargetLibraryDir();
        Path targetPackageDir = targetDirMain.resolve(compilerOptions.getTargetPackageMain().replace(".", File.separator));
        Files.createDirectories(targetPackageDir);
        Path targetLibraryDir = targetDirLibrary.resolve(compilerOptions.getTargetPackageLib().replace(".", File.separator));
        Files.createDirectories(targetLibraryDir);
        for (SourceFile sourceFile : sourceFiles) {
            generateCode(sourceFile, targetPackageDir, targetLibraryDir);
        }
        
        Logger.log(1, "Done; {0} Java files generated in {1}", sourceFiles.size(), targetPackageDir);
    }
    
    /**
     * Parse the given source file(s), usually a MODULE, and recursively parse all imported
     * DEFINITIONs and their IMPLEMENTATIONs.
     * <p>
     * The {@link #sourceFiles} ordered set will be filled with all parsed files, ordered in such a way
     * each file only depends on file(s) before it.
     */
    private void parseRecursive(SourceFile... mainModuleFiles) throws IOException {
        Set<SourceFile> parsed = new HashSet<>();
        List<SourceFile> toParse = new ArrayList<>();
        List<SourceFile> mainModuleFilesList = Arrays.asList(mainModuleFiles);
        toParse.addAll(mainModuleFilesList);
        while (!toParse.isEmpty()) {
            SourceFile sourceFile = toParse.remove(0);
            parsed.add(sourceFile);
            if (!SourceFileHelper.isDefinition(sourceFile) && !mainModuleFilesList.contains(sourceFile)) {
                // This is an implementation. Make it dependent from its definition
                SourceFile definitionSourceFile = SourceFileHelper.lookupDefinition(sourceFile, fileOptions);
                sourceFile.getDeps().add(definitionSourceFile);
            }
            
            // Parse it
            CompilationUnitContext cuContext = parse(sourceFile, (String dep) -> {
                SourceFile depFile = SourceFileHelper.lookupDefinition(sourceFile, dep, fileOptions);
                if (depFile != null) {
                    sourceFile.getDeps().add(depFile);
                    if (!parsed.contains(depFile) && !toParse.contains(depFile)) { // Not already parsed
                        toParse.add(depFile); // Add imported file to list of files to parse
                    }
                }
            });
            sourceFile.setCuContext(cuContext);
            sourceFiles.add(sourceFile);
            
            if (SourceFileHelper.isDefinition(sourceFile)) {
                // This is a DEFINITION. Add the corresponding IMPLEMENTATION to the list of files to compile
                SourceFile implementationSourceFile = SourceFileHelper.lookupImplementation(sourceFile, fileOptions);
                if (implementationSourceFile != null) {
                    toParse.add(implementationSourceFile);
                }
            }
        }
    }
    
    /**
     * Parse a single source file
     * @param m2sourceFile the source file to parse
     * @param onDependency the callback to invoke when another file is imported
     */
    private CompilationUnitContext parse(SourceFile m2sourceFile, Consumer<String> onDependency) throws IOException {
        Path path = m2sourceFile.getPath();
        Logger.log(2, "  {0}", path);

        CurrentFile.setCurrentFile(path);
        String content = Files.readString(path);
        
        // Step 1: pre-processing
        content = preProcess(content);
        
        // Step 2: lexer + parser
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        Lexer lexer = new m2pim4Lexer(CharStreams.fromStream(inputStream));
        TokenStream tokenStream = new CommonTokenStream(lexer);
        m2pim4Parser parser = new m2pim4Parser(tokenStream);
        
        parser.addParseListener(new m2pim4BaseListener() {

            /**
             * Detect IMPORTs of other source files
             */
            @Override
            public void exitImportList(ImportListContext ctx) {
                ParseTree importedElt = ctx.getChild(1); // 'FROM <name> IMPORT...' OR 'IMPORT <name>, <name>, ...'
                if (importedElt instanceof IdentContext identContext) {
                    String name = identContext.getText();
                    onDependency.accept(name);
                } else if (importedElt instanceof IdentListContext identListContext) {
                    for (int i = 0; i < identListContext.getChildCount(); i++) {
                        ParseTree child = identListContext.getChild(i);
                        if (child instanceof IdentContext) {
                            String name = child.getText();
                            onDependency.accept(name);
                        }
                    }
                } else {
                    throw new CompilationException(importedElt, "Expected identifier or identifier list");
                }
                super.exitImportList(ctx);
            }
            
        });
        CompilationUnitContext cuContext = parser.compilationUnit();
        return cuContext;
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
    
    /**
     * Create an abstract compilation unit (a {@link DefinitionModule}, {@link ImplementationModule}
     * or {@link Module}) from a parse tree (as a {@link CompilationUnitContext}).
     * @param module the parse tree of the compilation unit
     */
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
    
    /**
     * Generate Java code for the given Modula-2 source file. {@link SourceFile#getCompilationUnit()} must be
     * non-null, meaning that all required previous compilation steps have been performed.
     * @param sourceFile the Modula-2 source file for which to generate a Java file
     * @param targetPackageDir the target package for Java files corresponding to implementation modules and modules
     * @param targetLibraryDir the target package for Java files corresponding to definitions without implementation
     */
    private void generateCode(SourceFile sourceFile, Path targetPackageDir, Path targetLibraryDir) throws IOException {
        ICompilationUnit compilationUnit = sourceFile.getCompilationUnit();
        Path m2sourceFile = sourceFile.getPath();
        Logger.log(2, "  {0}", m2sourceFile);
        CurrentFile.setCurrentFile(m2sourceFile);
        
        // Step 5: code generation
        ResultContext result = new ResultContext(application.getCompilerOptions());
        
        if (compilationUnit instanceof DefinitionModule definitionModule) {
            Path m2implFile = m2sourceFile.resolveSibling(m2sourceFile.getFileName().toString().replace(".def", ".mod"));
            if (!Files.isRegularFile(m2implFile)) {
                // .def file (DEFINITION) has no corresponding .mod file (IMPLEMENTATION)
                Path javaImplFile = targetLibraryDir.resolve(definitionModule.getName() + ".java");
                if (!Files.isRegularFile(javaImplFile) || compilerOptions.isAlwaysOverrideStubs()) {
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
