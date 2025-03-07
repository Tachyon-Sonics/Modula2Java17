package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.BlockContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureDeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureHeadingContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StatementSequenceContext;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class ProcedureImplementationProcessor extends ProcessorBase {

    public void processProcedureImplementation(IHasScope scopeUnit, Consumer<ProcedureImplementation> procedureAdder, ProcedureDeclarationContext pdc) {
        expect(pdc, 0, ProcedureHeadingContext.class);
        expect(pdc, 1, ";");
        int nbChild = pdc.getChildCount();
        
        // Parse header
        ProcedureHeadingContext procedureHeadingContext = (ProcedureHeadingContext) pdc.getChild(0);
        ProcedureDeclarationProcessor processor = new ProcedureDeclarationProcessor(scopeUnit, procedureHeadingContext);
        ProcedureDefinition procedureDefinition = processor.process();
        ProcedureImplementation procedureImplementation = new ProcedureImplementation(loc(pdc), scopeUnit, 
                procedureDefinition.getName(), procedureDefinition.getReturnType());
        procedureImplementation.addArguments(procedureDefinition.getArguments());
        expect(pdc, nbChild - 1, procedureImplementation.getName());
        procedureAdder.accept(procedureImplementation);
        
        // Parse content
        for (int i = 2; i < nbChild - 1; i++) {
            ParseTree node = pdc.getChild(i);
            if (node instanceof BlockContext blockContext) {
                int nbChild1 = blockContext.getChildCount();
                expect(blockContext, nbChild1 - 3, "BEGIN");
                expect(blockContext, nbChild1 - 2, StatementSequenceContext.class);
                expect(blockContext, nbChild1 - 1, "END");
                
                // Declarations
                for (int j = 0; j < nbChild1 - 3; j++) {
                    ParseTree item = blockContext.getChild(j);
                    if (item instanceof DeclarationContext declarationContext) {
                        processDeclarationContext(procedureImplementation, procedureImplementation, declarationContext);
                    } else {
                        throw new UnexpectedTokenException(item);
                    }
                }
                
                // Body
                StatementSequenceContext statementSequence = (StatementSequenceContext) blockContext.getChild(nbChild1 - 2);
                List<IStatement> statements = new StatementsProcessor().processStatementSequence(procedureImplementation, statementSequence);
                procedureImplementation.addStatements(statements);
            } else {
                throw new UnexpectedTokenException(node);
            }
        }
    }
    
    private void processDeclarationContext(IHasScope scopeUnit, ProcedureImplementation procedureImplementation, DeclarationContext declarationContext) {
        if (declarationContext.getChild(0) instanceof ProcedureDeclarationContext procedureDeclarationContext) {
            // Nested procedure
            processProcedureImplementation(scopeUnit, procedureImplementation::addProcedure, procedureDeclarationContext);
            return;
        }
        
        expect(declarationContext, 0, TerminalNode.class);
        String terminal = declarationContext.getChild(0).getText();
        if (terminal.equals("CONST")) {
            ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(scopeUnit);
            List<ConstantDefinition> constantDefinitions = processor.process(declarationContext);
            procedureImplementation.addConstantDefinitions(constantDefinitions);
        } else if (terminal.equals("TYPE")) {
            TypeDeclarationsProcessor processor = new TypeDeclarationsProcessor(scopeUnit);
            List<TypeDefinition> typeDefinitions = processor.process(declarationContext);
            procedureImplementation.addTypeDefinitions(typeDefinitions);
        } else if (terminal.equals("VAR")) {
            VariableDeclarationsProcessor processor = new VariableDeclarationsProcessor(scopeUnit);
            List<VariableDefinition> variableDefinitions = new ArrayList<>();
            List<IType> nestedTypes = new ArrayList<>();
            processor.process(declarationContext, variableDefinitions, nestedTypes);
            new TypeDeclarationsProcessor(procedureImplementation).addNestedTypes(nestedTypes, procedureImplementation.getTypeDefinitions());
            procedureImplementation.addVariableDefinitions(variableDefinitions);
        } else {
            throw new UnexpectedTokenException(declarationContext.getChild(0));
        }
    }
    
}
