package ch.pitchtech.modula.converter.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.generator.statement.Statements;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.utils.StringUtils;

public class ImplementationModuleGenerator extends CompilationUnitGenerator {
    
    private final ImplementationModule implementationModule;
    private final DefinitionModule definitionModule;

    
    public ImplementationModuleGenerator(ImplementationModule implementationModule) {
        super(implementationModule, (SourceElement) implementationModule);
        this.implementationModule = implementationModule;
        this.definitionModule = implementationModule.getDefinition();
    }

    @Override
    public void generate(ResultContext result) {
        result.pushScope(definitionModule.getLocalScope());
        ResultContext definitionContent = result.subContext();
        generateDefinitionContent(definitionContent);
        result.popScope();
        result.pushScope(implementationModule.getLocalScope());
        ResultContext implementationContext = result.subContext();
        generateImplementationContent(implementationContext);
        ResultContext beginEndContext = result.subContext();
        generateBeginEnd(beginEndContext);
        result.popScope();
        
        result.writeLine("package " + result.getCompilerOptions().getTargetPackageMain() + ";");
        result.writeLn();
        
        List<String> javaImports = new ArrayList<>();
        addRequiredImports(definitionModule, definitionContent, javaImports);
        addRequiredImports(implementationModule, implementationContext, javaImports);
        Collections.sort(javaImports);
        writeImports(result, javaImports);
        
        String name = implementationModule.getName();
        result.writeLine("public class " + name + " {");
        result.writeLn();
        
        if (!result.getRequiredModuleInstances().isEmpty()) {
            result.incIndent();
            result.writeLine("// Imports");
            for (String moduleName : result.getRequiredModuleInstances()) {
                String varName = StringUtils.toCamelCase(moduleName);
                result.writeLine("private final " + moduleName + " " + varName + ";");
            }
            
            result.decIndent();
            result.writeLn();
            result.writeLn();
        }
        
        // Constructor
        result.incIndent();
        result.writeLine("private " + name + "() {");
        result.incIndent();
        result.writeLine("instance = this; // Set early to handle circular dependencies");
        for (String moduleName : result.getRequiredModuleInstances()) {
            String varName = StringUtils.toCamelCase(moduleName);
            result.writeLine(varName + " = " + moduleName + ".instance();");
        }
        result.decIndent();
        result.writeLine("}");
        result.decIndent();
        result.writeLn();
        result.writeLn();
        
        // Content
        result.write(definitionContent);
        result.write(implementationContext);
        
        // Factory
        result.incIndent();
        result.writeLine("// Support");
        result.writeLn();
        result.writeLine("private static " + name + " instance;");
        result.writeLn();
        result.writeLine("public static " + name + " instance() {");
        result.writeLine("    if (instance == null)");
        result.writeLine("        new " + name + "(); // will set 'instance'");
        result.writeLine("    return instance;");
        result.writeLine("}");
        result.writeLn();
        
        // Life-Cycle
        result.write(beginEndContext);
        result.decIndent();
        
        result.writeLine("}");
    }
    
    @Override
    protected void addRequiredImports(ICompilationUnit compilationUnit, ResultContext context, List<String> result) {
        for (String imp : context.getRequiredJavaImports()) {
            if (!result.contains(imp)) {
                result.add(imp);
            }
        }
        for (Import imp : compilationUnit.getImports()) {
            String fromModule = imp.getFromModule();
            if (!fromModule.equals("SYSTEM")) {
                DefinitionModule definitionModule = context.getScope().resolveModule(fromModule);
                if (!definitionModule.isImplemented()) {
                    String javaImport = context.getCompilerOptions().getTargetPackageLib() + "." + fromModule;
                    if (!result.contains(javaImport))
                        result.add(javaImport);
                }
            }
        }
    }

    static void writeImports(ResultContext result, Collection<String> requiredJavaImports) {
        if (!requiredJavaImports.isEmpty()) {
            List<String> javaImports = new ArrayList<>();
            List<String> otherImports = new ArrayList<>();
            for (String javaImport : requiredJavaImports) {
                if (javaImport.startsWith("java.")) {
                    javaImports.add(javaImport);
                } else {
                    otherImports.add(javaImport);
                }
            }
            
            for (String javaImport : javaImports) {
                result.writeLine("import " + javaImport + ";");
            }
            if (!javaImports.isEmpty() && !otherImports.isEmpty()) {
                result.writeLn();
            }
            for (String javaImport : otherImports) {
                result.writeLine("import " + javaImport + ";");
            }
            result.writeLn();
            result.writeLn();
        }
    }
    
    private void generateDefinitionContent(ResultContext result) {
        result.incIndent();
        
        generateConstants(definitionModule, result);
        generateTypes(definitionModule, result);
        generateVariables(definitionModule, result);

        result.decIndent();
    }
    
    private void generateImplementationContent(ResultContext result) {
        result.incIndent();
        
        generateConstants(implementationModule, result);
        generateTypes(implementationModule, result);
        generateVariables(implementationModule, result);
        generateProcedureImplementations(implementationModule, implementationModule.getProcedures(), result);

        result.decIndent();
    }
    
    private void generateBeginEnd(ResultContext result) {
        result.incIndent();
        result.writeLine("// Life-cycle");
        result.writeLn();
        result.writeLine("public void begin() {");
        result.incIndent();
        for (IStatement statement : implementationModule.getBeginStatements())
            Statements.generate(implementationModule, statement, result);
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        
        result.writeLine("public void close() {");
        result.incIndent();
        for (IStatement statement : implementationModule.getCloseStatements())
            Statements.generate(implementationModule, statement, result);
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        result.decIndent();
    }

}
