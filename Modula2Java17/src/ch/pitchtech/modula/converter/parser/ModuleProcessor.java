package ch.pitchtech.modula.converter.parser;

import org.antlr.v4.runtime.tree.ParseTree;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.BlockContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ImportListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProgramModuleContext;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.Module;

public class ModuleProcessor extends CompilationUnitProcessor {

    private final Application application;


    public ModuleProcessor(Application application) {
        this.application = application;
    }

    public Module process(ProgramModuleContext programModule) {
        int nbChild = programModule.getChildCount();
        expect(programModule, 0, "MODULE");
        String name = programModule.getChild(1).getText();
        expect(programModule, 2, ";");
        
        expect(programModule, nbChild - 2, name);
        expect(programModule, nbChild - 1, ".");
        
        Module module = new Module(loc(programModule), name, application);
        application.addModule(module);
        
        for (int i = 3; i < nbChild - 2; i++) {
            ParseTree node = programModule.getChild(i);
            processRootElement(module, node);
        }
        return module;
    }
   
    private void processRootElement(Module compilationUnit, ParseTree node) {
        if (node instanceof ImportListContext importList) {
            processImports(compilationUnit, node, importList);
        } else if (node instanceof BlockContext blockContext) {
            processBlockContext(compilationUnit, compilationUnit::addProcedure, compilationUnit.getBeginStatements(), blockContext);
        } else {
            throw new UnexpectedTokenException(node);
        }
    }

}
