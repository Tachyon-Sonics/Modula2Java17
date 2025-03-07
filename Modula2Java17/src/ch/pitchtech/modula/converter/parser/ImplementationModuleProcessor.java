package ch.pitchtech.modula.converter.parser;

import org.antlr.v4.runtime.tree.ParseTree;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.BlockContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ImportListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProgramModuleContext;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ImplementationModule;

public class ImplementationModuleProcessor extends CompilationUnitProcessor {
    
    private final Application application;


    public ImplementationModuleProcessor(Application application) {
        this.application = application;
    }
    
    public ImplementationModule process(ProgramModuleContext programModule) {
        int nbChild = programModule.getChildCount();
        expect(programModule, 0, "MODULE");
        String name = programModule.getChild(1).getText();
        expect(programModule, 2, ";");
        
        expect(programModule, nbChild - 2, name);
        expect(programModule, nbChild - 1, ".");
        
        DefinitionModule definitionModule = application.getScope().resolveModule(name);
        if (definitionModule == null)
            throw new UnexpectedTokenException(programModule, "Definition module not found: " + name);
        ImplementationModule implementation = new ImplementationModule(loc(programModule), definitionModule, application);
        application.addImplementationModule(implementation);
        
        for (int i = 3; i < nbChild - 2; i++) {
            ParseTree node = programModule.getChild(i);
            processRootElement(implementation, node);
        }
        return implementation;
    }
   
    private void processRootElement(ImplementationModule compilationUnit, ParseTree node) {
        if (node instanceof ImportListContext importList) {
            processImports(compilationUnit, node, importList);
        } else if (node instanceof BlockContext blockContext) {
            processBlockContext(compilationUnit, compilationUnit::addProcedure, compilationUnit.getBeginStatements(), blockContext);
        } else {
            throw new UnexpectedTokenException(node);
        }
    }

}
