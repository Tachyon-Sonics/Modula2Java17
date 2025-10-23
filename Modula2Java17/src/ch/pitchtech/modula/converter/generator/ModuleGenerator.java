package ch.pitchtech.modula.converter.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.statement.Statements;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.Module;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.utils.StringUtils;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;

public class ModuleGenerator extends CompilationUnitGenerator {
    
    private final Module module;
    

    public ModuleGenerator(Module module) {
        super(module, (SourceElement) module);
        this.module = module;
    }

    @Override
    public void generate(ResultContext result) {
        result.pushScope(module.getLocalScope());
        ResultContext content = result.subContext();
        generateContent(content);
        result.writeLine("package " + result.getCompilerOptions().getTargetPackageMain() + ";");
        result.writeLn();
        List<String> requiredJavaImports = super.getRequiredImports(module, result, 
                result.getCompilerOptions().getTargetPackageMain());
        ImplementationModuleGenerator.writeImports(result, requiredJavaImports);
        result.writeLine("public class " + module.getName() + " {");
        result.writeLn();
        if (!result.getRequiredModuleInstances().isEmpty()) {
            result.incIndent();
            result.writeLine("// Imports");
            for (String moduleName : result.getRequiredModuleInstances()) {
                String varName = StringUtils.toCamelCase(moduleName);
                result.writeLine("private final " + moduleName + " " + varName + " = " + moduleName + ".instance();");
            }
            result.decIndent();
            result.writeLn();
            result.writeLn();
        }
        result.write(content);
    }
    
    private void generateContent(ResultContext result) {
        result.incIndent();
        
        super.generateConstants(module, result);
        super.generateTypes(module, result);
        super.generateVariables(module, result);
        super.generateProcedureImplementations(module, module.getProcedures(), result);
        
        List<DefinitionModule> dependencies = getDependencies();
        
        result.writeLine("// Life Cycle");
        result.writeLn();
        result.writeLine("private void begin() {");
        result.incIndent();
        for (DefinitionModule definitionModule : dependencies) {
            result.writeLine(definitionModule.getName() + ".instance().begin();");
        }
        if (!dependencies.isEmpty() && !module.getBeginStatements().isEmpty())
            result.writeLn();
        for (IStatement statement : module.getBeginStatements()) {
            if (statement instanceof SourceElement sourceElement) {
                result.writeCommentsFor(sourceElement.getSourceLocation());
            }
            Statements.getGenerator(module, statement).generate(result);
        }
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        
        result.writeLine("private void close() {");
        result.incIndent();
        for (IStatement statement : module.getCloseStatements()) {
            if (statement instanceof SourceElement sourceElement) {
                result.writeCommentsFor(sourceElement.getSourceLocation());
            }
            Statements.getGenerator(module, statement).generate(result);
        }
        if (!dependencies.isEmpty() && !module.getCloseStatements().isEmpty())
            result.writeLn();
        for (int i = dependencies.size() - 1; i >= 0; i--) {
            DefinitionModule definitionModule = dependencies.get(i);
            result.writeLine(definitionModule.getName() + ".instance().close();");
        }
        result.decIndent();
        result.writeLine("}");
        result.writeLn();
        
        result.writeLine("public static void main(String[] args) {");
        result.incIndent();
        result.ensureJavaImport(Runtime.class);
        result.ensureJavaImport(HaltException.class);
        result.writeLine("Runtime.setArgs(args);");
        result.writeLine(module.getName() + " instance = new " + module.getName() + "();");
        result.writeLine("try {");
        result.writeLine("    instance.begin();");
        result.writeLine("} catch (HaltException ex) {");
        result.writeLine("    // Normal termination");
        result.writeLine("} catch (Throwable ex) {");
        result.writeLine("    ex.printStackTrace();");
        result.writeLine("} finally {");
        result.writeLine("    instance.close();");
        result.writeLine("}");
        result.decIndent();
        result.writeLine("}");
        result.writeLn();

        result.decIndent();
        result.popScope();
        result.writeLine("}");
    }
    
    private List<DefinitionModule> getDependencies() {
        Set<DefinitionModule> visited = new HashSet<>();
        List<DefinitionModule> result = new ArrayList<>();
        collectDependencies(module, visited, result);
        return result;
    }
    
    private void collectDependencies(ICompilationUnit cu, Set<DefinitionModule> visited, List<DefinitionModule> result) {
        for (Import item : cu.getImports())
            collectDependencies(cu, item, visited, result);
        if (cu instanceof DefinitionModule definitionModule) {
            ImplementationModule implementation = definitionModule.getImplementation();
            if (implementation != null) {
                collectDependencies(implementation, visited, result);
            }
        }
    }
    
    private void collectDependencies(ICompilationUnit cu, Import imp, Set<DefinitionModule> visited, List<DefinitionModule> result) {
        DefinitionModule definitionModule = cu.getScope().resolveModule(imp.getFromModule());
        if (definitionModule == null)
            throw new CompilationException(imp, "Could not resolve definition module {0}", imp.getFromModule());
        if (!visited.contains(definitionModule) && !definitionModule.getName().equals("SYSTEM")) {
            visited.add(definitionModule);
            collectDependencies(definitionModule, visited, result);
            if (!result.contains(definitionModule))
                result.add(definitionModule);
        }
    }

}
