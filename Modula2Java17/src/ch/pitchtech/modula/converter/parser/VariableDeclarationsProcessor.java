package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.Type_Context;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.VariableDeclarationContext;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.utils.StringUtils;

public class VariableDeclarationsProcessor extends ProcessorBase {
    
    private final IHasScope scopeUnit;
    
    
    public VariableDeclarationsProcessor(IHasScope scopeUnit) {
        this.scopeUnit = scopeUnit;
    }
    
    public void process(DefinitionContext definition, List<VariableDefinition> result, List<IType> nestedTypes) {
        processImpl(definition, result, nestedTypes);
    }
    
    public void process(DeclarationContext declaration, List<VariableDefinition> result, List<IType> nestedTypes) {
        processImpl(declaration, result, nestedTypes);
    }
    
    private void processImpl(ParserRuleContext definition, List<VariableDefinition> result, List<IType> nestedTypes) {
        expect(definition, 0, "VAR");
        for (int i = 1; i < definition.getChildCount(); i++) {
            ParseTree child = definition.getChild(i);
            if (child instanceof TerminalNodeImpl terminal) {
                expectText(terminal, ";");
            } else if (child instanceof VariableDeclarationContext variableDeclarationContext) {
                expect(variableDeclarationContext, 0, IdentListContext.class);
                expect(variableDeclarationContext, 1, ":");
                expect(variableDeclarationContext, 2, Type_Context.class);
                
                IdentListContext identListContext = (IdentListContext) variableDeclarationContext.getChild(0);
                List<String> identNames = new ArrayList<>();
                for (ParseTree identItem : identListContext.children) {
                    if (identItem instanceof IdentContext identMember) {
                        String name = identMember.getText();
                        identNames.add(name);
                    } else if (identItem instanceof TerminalNodeImpl terminal) {
                        expectText(terminal, ",");
                    } else {
                        throw new UnexpectedTokenException(identItem);
                    }
                }
                Type_Context typeContext0 = (Type_Context) variableDeclarationContext.getChild(2);
                String typeName = "";
                for (String identName : identNames)
                    typeName += "_" + StringUtils.toPascalCase(identName);
                List<IType> types = new TypeDeclarationsProcessor(scopeUnit).processType(typeName, typeContext0);
                for (IType type : types) {
                    if (Types.requiresExplicitDeclaration(type))
                        nestedTypes.add(type);
                }
                IType variableType = TypeDeclarationsProcessor.primary(types);
                for (String identName : identNames) {
                    VariableDefinition variableDefinition = new VariableDefinition(loc(variableDeclarationContext),
                            scopeUnit, identName, variableType);
                    result.add(variableDefinition);
                }
            } else {
                throw new UnsupportedOperationException(child.getClass().getSimpleName());
            }
        }
    }

}
