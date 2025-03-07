package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionModuleContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ImportListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureHeadingContext;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class DefinitionModuleProcessor extends CompilationUnitProcessor {
    
    private final Application application;

    
    public DefinitionModuleProcessor(Application application) {
        this.application = application;
    }
    
    public DefinitionModule process(DefinitionModuleContext definitionModule) {
        int nbChild = definitionModule.getChildCount();
        expect(definitionModule, 0, "DEFINITION");
        expect(definitionModule, 1, "MODULE");
        expect(definitionModule, 2, IdentContext.class);
        expect(definitionModule, 3, ";");
        
        String name = definitionModule.getChild(2).getText();
        expect(definitionModule, nbChild - 3, "END");
        expect(definitionModule, nbChild - 2, name);
        expect(definitionModule, nbChild - 1, ".");

        DefinitionModule dm = new DefinitionModule(loc(definitionModule), name, application);
        application.addDefinitionModule(dm);
        
        for (int i = 4; i < nbChild - 3; i++) {
            ParseTree node = definitionModule.getChild(i);
            processBlock(dm, node);
        }
        return dm;
    }

    private void processBlock(ICompilationUnit compilationUnit, ParseTree node) {
        if (node instanceof ImportListContext importList) {
            processImports(compilationUnit, node, importList);
        } else if (node instanceof DefinitionContext definition) {
            if (definition.getChild(0) instanceof ProcedureHeadingContext procedureHeadingContext) {
                if (compilationUnit instanceof DefinitionModule definitionModule) {
                    ProcedureDeclarationProcessor processor = new ProcedureDeclarationProcessor(definitionModule, procedureHeadingContext);
                    ProcedureDefinition procedureDefinition = processor.process();
                    definitionModule.getProcedureDefinitions().add(procedureDefinition);
                } else {
                    throw new UnexpectedTokenException(definition.getChild(0), "No procedure definition expected in an implementation");
                }
            } else {
                expect(definition, 0, TerminalNodeImpl.class);
                String terminal = definition.getChild(0).getText();
                if (terminal.equals("CONST")) {
                    ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(compilationUnit);
                    List<ConstantDefinition> constantDefinitions = processor.process(definition);
                    compilationUnit.getConstantDefinitions().addAll(constantDefinitions);
                } else if (terminal.equals("TYPE")) {
                    TypeDeclarationsProcessor processor = new TypeDeclarationsProcessor(compilationUnit);
                    List<TypeDefinition> typeDefinitions = processor.process(definition);
                    compilationUnit.getTypeDefinitions().addAll(typeDefinitions);
                } else if (terminal.equals("VAR")) {
                    VariableDeclarationsProcessor processor = new VariableDeclarationsProcessor(compilationUnit);
                    List<VariableDefinition> variableDefinitions = new ArrayList<>();
                    List<IType> nestedTypes = new ArrayList<>();
                    processor.process(definition, variableDefinitions, nestedTypes);
                    new TypeDeclarationsProcessor(compilationUnit).addNestedTypes(nestedTypes, compilationUnit.getTypeDefinitions());
                    compilationUnit.getVariableDefinitions().addAll(variableDefinitions);
                } else {
                    throw new UnexpectedTokenException(definition.getChild(0));
                }
            }
        } else {
            throw new UnexpectedTokenException(node);
        }
    }

}
