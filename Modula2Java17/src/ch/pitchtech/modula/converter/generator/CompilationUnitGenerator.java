package ch.pitchtech.modula.converter.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.generator.field.ConstantDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.procedure.ProcedureImplementationGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeDefinitionGenerator;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public abstract class CompilationUnitGenerator extends Generator {
    
    public CompilationUnitGenerator(ICompilationUnit compilationUnit, SourceElement forSourceElement) {
        super(compilationUnit, forSourceElement);
    }
    
    protected List<String> getRequiredImports(ICompilationUnit compilationUnit, ResultContext context, String currentPackage) {
        List<String> result = new ArrayList<>();
        if (!currentPackage.equals(TARGET_PACKAGE_LIBRARY)) {
            if (compilationUnit instanceof ch.pitchtech.modula.converter.model.Module) {
                addRequiredJavaImports(context, result);
                String javaImport = TARGET_PACKAGE_LIBRARY + ".*";
                result.add(javaImport);
            } else {
                addRequiredImports(compilationUnit, context, result);
            }
        }
        Collections.sort(result);
        return result;
    }
    
    protected void addRequiredImports(ICompilationUnit compilationUnit, ResultContext context, List<String> result) {
        addRequiredJavaImports(context, result);
        for (Import imp : compilationUnit.getImports()) {
            String fromModule = imp.getFromModule();
            if (!fromModule.equals("SYSTEM")) {
                DefinitionModule definitionModule = context.getScope().resolveModule(fromModule);
                if (!definitionModule.isImplemented()) {
                    String javaImport = TARGET_PACKAGE_LIBRARY + "." + fromModule;
                    if (!result.contains(javaImport))
                        result.add(javaImport);
                }
            }
        }
    }

    private void addRequiredJavaImports(ResultContext context, List<String> result) {
        for (String imp : context.getRequiredJavaImports()) {
            if (!result.contains(imp)) {
                result.add(imp);
            }
        }
    }

    protected void generateConstants(ICompilationUnit compilationUnit, ResultContext result) {
        if (!compilationUnit.getConstantDefinitions().isEmpty()) {
            result.writeLine("// CONST");
            result.writeLn();
            for (ConstantDefinition constantDefinition : compilationUnit.getConstantDefinitions()) {
                new ConstantDefinitionGenerator(constantDefinition, compilationUnit).generate(result);
            }
            result.writeLn();
            result.writeLn();
        }
    }
    
    protected void generateTypes(ICompilationUnit compilationUnit, ResultContext result) {
        ResultContext typeResult = result.subContext();
        if (!compilationUnit.getTypeDefinitions().isEmpty()) {
            for (TypeDefinition typeDefinition : compilationUnit.getTypeDefinitions()) {
                ResultContext typeContext = result.subContext();
                new TypeDefinitionGenerator(compilationUnit, typeDefinition).generate(typeContext);
                if (!typeContext.toString().isBlank()) {
                    typeResult.write(typeContext);
                    typeResult.writeLn();
                }
            }
        }
        if (!typeResult.toString().isBlank()) {
            result.writeLine("// TYPE");
            result.writeLn();
            result.write(typeResult);
            result.writeLn();
        }
    }
    
    protected void generateVariables(ICompilationUnit compilationUnit, ResultContext result) {
        if (!compilationUnit.getVariableDefinitions().isEmpty()) {
            result.writeLine("// VAR");
            result.writeLn();
            for (VariableDefinition variableDefinition : compilationUnit.getVariableDefinitions()) {
                new VariableDefinitionGenerator(compilationUnit, variableDefinition).generate(result);
            }
            result.writeLn();
            result.writeLn();
            for (VariableDefinition variableDefinition : compilationUnit.getVariableDefinitions()) {
                new VariableDefinitionGenerator(compilationUnit, variableDefinition).generateGetterAndSetter(result);
            }
            result.writeLn();
        }
    }
    
    protected void generateProcedureImplementations(ICompilationUnit compilationUnit, List<ProcedureImplementation> procedures, ResultContext result) {
        if (!procedures.isEmpty()) {
            result.writeLine("// PROCEDURE");
            result.writeLn();
            for (ProcedureImplementation procedure : procedures) {
                new ProcedureImplementationGenerator(compilationUnit, procedure).generate(result);
            }
            result.writeLn();
        }
    }

}
