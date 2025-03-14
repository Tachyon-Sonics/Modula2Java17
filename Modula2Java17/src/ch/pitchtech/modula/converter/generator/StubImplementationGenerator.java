package ch.pitchtech.modula.converter.generator;

import ch.pitchtech.modula.converter.generator.procedure.ProcedureStubGenerator;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.source.SourceElement;

/**
 * Generate stub code for an IMPLEMENTATION MODULE that does not exist (but whose DEFINITION MODULE exists)
 */
public class StubImplementationGenerator extends CompilationUnitGenerator {

    private final DefinitionModule definitionModule;

    
    public StubImplementationGenerator(DefinitionModule definitionModule) {
        super(definitionModule, (SourceElement) definitionModule);
        this.definitionModule = definitionModule;
    }

    @Override
    public void generate(ResultContext result) {
        result.pushScope(definitionModule.getLocalScope());
        ResultContext content = result.subContext();
        generateContent(content);
        result.writeLine("package " + TARGET_PACKAGE_LIBRARY + ";");
        result.writeLn();
        ImplementationModuleGenerator.writeImports(result, result.getRequiredJavaImports());
        result.write(content);
    }
    
    private void generateContent(ResultContext result) {
        String name = definitionModule.getName();
        result.writeLine("public class " + name + " {");
        result.writeLn();
        
        // Constructor
        result.incIndent();
        result.writeLine("private static " + name + " instance;");
        result.writeLn();
        result.writeLn();
        result.writeLine("private " + name + "() {");
        result.writeLine("    instance = this; // Set early to handle circular dependencies");
        result.writeLine("}");
        result.writeLn();
        result.writeLine("public static " + name + " instance() {");
        result.writeLine("    if (instance == null)");
        result.writeLine("        new " + name + "(); // will set 'instance'");
        result.writeLine("    return instance;");
        result.writeLine("}");
        result.writeLn();
        result.writeLn();
        
        generateConstants(definitionModule, result);
        generateTypes(definitionModule, result);
        generateVariables(definitionModule, result);

        // Stub implementations
        result.writeLine("// PROCEDURE");
        result.writeLn();
        for (ProcedureDefinition procedureDefinition : definitionModule.getProcedureDefinitions()) {
            new ProcedureStubGenerator(definitionModule, procedureDefinition).generate(result);
        }
        result.writeLn();
        result.writeLine("public void begin() {");
        result.writeLn();
        result.writeLine("}");

        result.writeLn();
        result.writeLine("public void close() {");
        result.writeLn();
        result.writeLine("}");

        result.decIndent();
        result.popScope();
        result.writeLine("}");
    }
    
}
