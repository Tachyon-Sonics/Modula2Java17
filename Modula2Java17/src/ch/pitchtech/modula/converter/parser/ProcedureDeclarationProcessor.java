package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FormalParametersContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FormalTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FpSectionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureHeadingContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.QualidentContext;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;

public class ProcedureDeclarationProcessor extends ProcessorBase {
    
    private final IHasScope scopeUnit;
    private final ProcedureHeadingContext procedureHeadingContext;

    
    public ProcedureDeclarationProcessor(IHasScope scopeUnit, ProcedureHeadingContext procedureHeadingContext) {
        this.scopeUnit = scopeUnit;
        this.procedureHeadingContext = procedureHeadingContext;
    }

    public ProcedureDefinition process() {
        expect(procedureHeadingContext, 0, "PROCEDURE");
        expect(procedureHeadingContext, 1, IdentContext.class);
        boolean hasParentheses = (procedureHeadingContext.getChildCount() > 2);
        if (hasParentheses)
            expect(procedureHeadingContext, 2, FormalParametersContext.class);
        IdentContext identContextName = (IdentContext) procedureHeadingContext.getChild(1);
        String procedureName = identContextName.getText();
        boolean hasReturnType = false;
        boolean hasArguments = false;
        
        LiteralType returnType = null;
        List<FormalArgument> formalArguments = new ArrayList<>();
        
        if (hasParentheses) {
            FormalParametersContext formalParametersContext = (FormalParametersContext) procedureHeadingContext.getChild(2);
            int fpcSize = formalParametersContext.getChildCount();
            hasReturnType = formalParametersContext.getChild(fpcSize - 2).getText().equals(":");
            int lastArgumentIndex; // inclusive
            if (hasReturnType) {
                expect(formalParametersContext, 0, "(");
                if (formalParametersContext.getChild(1).getText().equals(")")) {
                    // Empty argument list
                    hasArguments = false;
                    lastArgumentIndex = 0;
                } else {
                    hasArguments = true;
                    expect(formalParametersContext, 1, FpSectionContext.class);
                    lastArgumentIndex = fpcSize - 4;
                }
                expect(formalParametersContext, fpcSize - 3, ")");
                expect(formalParametersContext, fpcSize - 2, ":");
                expect(formalParametersContext, fpcSize - 1, QualidentContext.class);
            } else {
                expect(formalParametersContext, 0, "(");
                if (formalParametersContext.getChild(1).getText().equals(")")) {
                    // Empty argument list
                    hasArguments = false;
                    lastArgumentIndex = 0;
                } else {
                    hasArguments = true;
                    expect(formalParametersContext, 1, FpSectionContext.class);
                    expect(formalParametersContext, fpcSize - 1, ")");
                    lastArgumentIndex = fpcSize - 2;
                }
            }
            // Arguments
            if (hasArguments) {
                for (int i = 1; i <= lastArgumentIndex; i++) {
                    if (formalParametersContext.getChild(i) instanceof TerminalNodeImpl terminal) {
                        expectText(terminal, ";");
                    } else {
                        boolean isVar = false;
                        int identListStartIndex = 0;
                        FpSectionContext fpSectionContext = (FpSectionContext) formalParametersContext.getChild(i);
                        if (fpSectionContext.getChild(0) instanceof TerminalNodeImpl terminal) {
                            expectText(terminal, "VAR");
                            identListStartIndex = 1;
                            isVar = true;
                            expect(fpSectionContext, 1, IdentListContext.class);
                            expect(fpSectionContext, 2, ":");
                            expect(fpSectionContext, 3, FormalTypeContext.class);
                        } else {
                            expect(fpSectionContext, 0, IdentListContext.class);
                            expect(fpSectionContext, 1, ":");
                            expect(fpSectionContext, 2, FormalTypeContext.class);
                        }
                        
                        // Collect argument names
                        List<String> paramNames = new ArrayList<>();
                        IdentListContext identListContext = (IdentListContext) fpSectionContext.getChild(identListStartIndex);
                        for (int j = 0; j < identListContext.getChildCount(); j++) {
                            if (identListContext.getChild(j) instanceof TerminalNodeImpl terminal) {
                                expectText(terminal, ",");
                            } else if (identListContext.getChild(j) instanceof IdentContext identContext) {
                                paramNames.add(identContext.getText());
                            } else {
                                throw new UnexpectedTokenException(identListContext.getChild(j));
                            }
                        }
                        
                        // Collect type
                        FormalTypeContext formalTypeContext = (FormalTypeContext) fpSectionContext.getChild(identListStartIndex + 2);
                        int nbItems = formalTypeContext.getChildCount();
                        int typeIndex = 0;
                        boolean isOpenArray = false;
                        if (nbItems > 1) {
                            assert nbItems == 3;
                            expect(formalTypeContext, 0, "ARRAY");
                            expect(formalTypeContext, 1, "OF");
                            isOpenArray = true;
                            typeIndex = 2;
                        }
                        expect(formalTypeContext, typeIndex, QualidentContext.class);
                        QualidentContext qualidentContext = (QualidentContext) formalTypeContext.getChild(typeIndex);
                        expect(qualidentContext, 0, IdentContext.class);
                        IdentContext identContextType = (IdentContext) qualidentContext.getChild(0);
                        LiteralType lType = new LiteralType(loc(identContextType), scopeUnit, identContextType.getText(), false);
                        IType type;
                        if (isOpenArray)
                            type = new OpenArrayType(loc(identContextType), scopeUnit, lType);
                        else
                            type = lType;
                        
                        // Add arguments
                        for (String paramName : paramNames) {
                            FormalArgument formalArgument = new FormalArgument(loc(identListContext), isVar, paramName, type);
                            formalArguments.add(formalArgument);
                        }
                    }
                }
            }
            if (hasReturnType) {
                // TODO all QualidentContext can be "x" or "x.x" or "x.x.x", etc
                QualidentContext returnQualidentContext = (QualidentContext) formalParametersContext.getChild(fpcSize - 1);
                expect(returnQualidentContext, 0, IdentContext.class);
                IdentContext returnIdent = (IdentContext) returnQualidentContext.getChild(0);
                returnType = new LiteralType(loc(returnIdent), scopeUnit, returnIdent.getText(), false);
            }
        }
        
        ProcedureDefinition procedureDefinition = new ProcedureDefinition(loc(procedureHeadingContext),
                scopeUnit, procedureName, returnType, false);
        procedureDefinition.addArguments(formalArguments);
        return procedureDefinition;
    }
}
