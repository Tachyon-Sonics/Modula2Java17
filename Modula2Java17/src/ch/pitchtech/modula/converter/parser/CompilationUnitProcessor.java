package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.BlockContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ImportListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureDeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.StatementSequenceContext;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.Import;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public abstract class CompilationUnitProcessor extends ProcessorBase {

    protected void processImports(ICompilationUnit compilationUnit, ParseTree node, ImportListContext importList) {
        if (node.getChild(0).getText().equals("FROM")) { // FROM <module> IMPORT <ident>, ...
            expect(importList, 0, "FROM");
            expect(importList, 1, IdentContext.class);
            expect(importList, 2, "IMPORT");
            expect(importList, 3, IdentListContext.class);
            expect(importList, 4, ";");
            String fromModule = importList.getChild(1).getText();
            IdentListContext identList = (IdentListContext) importList.getChild(3);
            List<String> importItems = new ArrayList<>();
            for (ParseTree item0 : identList.children) {
                if (item0 instanceof IdentContext ident)
                    importItems.add(ident.getText());
            }
            Import item = new Import(loc(importList), compilationUnit, fromModule, false);
            item.getItems().addAll(importItems);
            compilationUnit.getImports().add(item);
        } else { // IMPORT <module>
            expect(importList, 0, "IMPORT");
            expect(importList, 1, IdentListContext.class);
            expect(importList, 2, ";");
            IdentListContext identListContext = (IdentListContext) importList.getChild(1);
            for (int i = 0; i < identListContext.getChildCount(); i++) {
                ParseTree child = identListContext.getChild(i);
                if (child instanceof IdentContext identContext) {
                    String moduleName = identContext.getText();
                    Import item = new Import(loc(importList), compilationUnit, moduleName, true);
                    compilationUnit.getImports().add(item);
                } else if (child instanceof TerminalNode terminal) {
                    expectText(terminal, ",");
                } else {
                    throw new UnexpectedTokenException(child);
                }
            }
        }
    }

    protected void processBlockContext(ICompilationUnit compilationUnit, 
            Consumer<ProcedureImplementation> procedureAdder, List<IStatement> beginStatements, BlockContext blockContext) {
        boolean inBeginBlock = false;
        for (int i = 0; i < blockContext.getChildCount(); i++) {
            ParseTree node = blockContext.getChild(i);
            if (node instanceof DeclarationContext declarationContext) {
                ParseTree decl = declarationContext.getChild(0);
                if (decl instanceof ProcedureDeclarationContext procedureDeclarationContext) {
                    expect(declarationContext, 1, ";");
                    new ProcedureImplementationProcessor().processProcedureImplementation(compilationUnit,
                            procedureAdder, procedureDeclarationContext);
                } else if (decl instanceof TerminalNode terminal) {
                    String text = terminal.getText();
                    if (text.equals("VAR")) {
                        VariableDeclarationsProcessor processor = new VariableDeclarationsProcessor(compilationUnit);
                        List<VariableDefinition> variableDefinitions = new ArrayList<>();
                        List<IType> nestedTypes = new ArrayList<>();
                        processor.process(declarationContext, variableDefinitions, nestedTypes);
                        new TypeDeclarationsProcessor(compilationUnit).addNestedTypes(nestedTypes, compilationUnit.getTypeDefinitions());
                        compilationUnit.getVariableDefinitions().addAll(variableDefinitions);
                    } else if (text.equals("CONST")) {
                        ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(compilationUnit);
                        List<ConstantDefinition> constantDefinitions = processor.process(declarationContext);
                        compilationUnit.getConstantDefinitions().addAll(constantDefinitions);
                    } else if (text.equals("TYPE")) {
                        TypeDeclarationsProcessor processor = new TypeDeclarationsProcessor(compilationUnit);
                        List<TypeDefinition> typeDefinitions = processor.process(declarationContext);
                        compilationUnit.getTypeDefinitions().addAll(typeDefinitions);
                    } else {
                        throw new UnexpectedTokenException(decl);
                    }
                } else {
                    throw new UnexpectedTokenException(decl);
                }
                
            } else if (node instanceof TerminalNode terminal) {
                String text = terminal.getText();
                if (text.equals("BEGIN")) {
                    if (inBeginBlock)
                        throw new UnexpectedTokenException(terminal);
                    inBeginBlock = true;
                } else if (text.equals("END")) {
                    inBeginBlock = false;
                } else {
                    throw new UnexpectedTokenException(terminal);
                }
            } else if (node instanceof StatementSequenceContext statementSequence) {
                if (!inBeginBlock)
                    throw new UnexpectedTokenException(statementSequence);
                List<IStatement> statements = new StatementsProcessor().processStatementSequence(compilationUnit, statementSequence);
                beginStatements.addAll(statements);
            } else {
                throw new UnexpectedTokenException(node);
            }
        }
    }
    
}
