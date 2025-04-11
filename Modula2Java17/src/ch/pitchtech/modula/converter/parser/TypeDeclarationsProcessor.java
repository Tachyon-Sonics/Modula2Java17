package ch.pitchtech.modula.converter.parser;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ArrayTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CaseLabelListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ConstExpressionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.DefinitionContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.EnumerationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FieldListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FieldListSequenceContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FormalTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.FormalTypeListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.IdentListContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.PointerTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.ProcedureTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.QualidentContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.RecordTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SetTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SimpleTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.SubrangeTypeContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.TypeDeclarationContext;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.Type_Context;
import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.VariantContext;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.CaseVariantType;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.INamedType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.converter.utils.StringUtils;

public class TypeDeclarationsProcessor extends ProcessorBase {
    
    private final IHasScope scopeUnit;
    
    
    public TypeDeclarationsProcessor(IHasScope scopeUnit) {
        this.scopeUnit = scopeUnit;
    }
    
    public static IType primary(List<IType> types) {
        return types.get(types.size() - 1);
    }
    
    public static List<IType> single(IType type) {
        ArrayList<IType> result = new ArrayList<>();
        result.add(type);
        return result;
    }

    public List<TypeDefinition> process(DefinitionContext definition) {
        expect(definition, 0, "TYPE");
        List<TypeDefinition> result = new ArrayList<>();
        int i = 1;
        while (i < definition.getChildCount()) {
            expect(definition, i + 0, IdentContext.class);
            IdentContext ident = (IdentContext) definition.getChild(i + 0);
            String typeName = ident.getText();
            if (definition.getChild(i + 1).getText().equals(";")) {
                // Opaque TYPE
                TypeDefinition opaqueTypeDefinition = new TypeDefinition(loc(ident), scopeUnit, typeName);
                result.add(opaqueTypeDefinition);
                i+= 2;
            } else {
                // Regular TYPE
                expect(definition, i + 1, "=");
                Type_Context typeContext = (Type_Context) definition.getChild(i + 2);
                List<IType> types = processType(typeName, typeContext);
                IType mainType = primary(types);
                for (IType type : types) {
                    TypeDefinition typeDefinition = new TypeDefinition(loc(ident), scopeUnit, 
                            type == mainType ? typeName : "<nested>", type);
                    result.add(typeDefinition);
                }
                expect(definition, i + 3, ";");
                i+= 4;
            }
        }
        return result;
    }
    
    public List<TypeDefinition> process(DeclarationContext definition) {
        expect(definition, 0, "TYPE");
        List<TypeDefinition> result = new ArrayList<>();
        for (int i = 1; i < definition.getChildCount(); i++) {
            ParseTree node = definition.getChild(i);
            if (node instanceof TypeDeclarationContext typeDeclarationContext) {
                expectNbChild(typeDeclarationContext, 3);
                expect(typeDeclarationContext, 0, IdentContext.class);
                expect(typeDeclarationContext, 1, "=");
                expect(typeDeclarationContext, 2, Type_Context.class);
                IdentContext identContext = (IdentContext) typeDeclarationContext.getChild(0);
                Type_Context typeContext = (Type_Context) typeDeclarationContext.getChild(2);
                String typeName = identContext.getText();
                List<IType> types = processType(typeName, typeContext);
                IType mainType = primary(types);
                for (IType type : types) {
                    TypeDefinition typeDefinition = new TypeDefinition(loc(identContext), scopeUnit,
                            type == mainType ? typeName : "<nested>", type);
                    result.add(typeDefinition);
                }
            }
        }
        return result;
    }
    
    List<IType> processType(String typeName, Type_Context typeContext) {
        if (typeContext.getChild(0) instanceof SimpleTypeContext simpleType) {
            if (simpleType.getChild(0) instanceof QualidentContext qualident) {
                if (qualident.getChild(0) instanceof IdentContext typeIdent) {
                    String identName = typeIdent.getText();
                    // Note: if the name correspond to a built-in type, ScopeResolver will "resolve" it to
                    // the corresponding built-in type. Hence we set 'builtin' to false here:
                    return single(new LiteralType(loc(typeIdent), scopeUnit, identName, false));
                } else {
                    throw new UnexpectedTokenException(qualident.getChild(0));
                }
            } else if (simpleType.getChild(0) instanceof EnumerationContext enumerationContext) {
                expect(enumerationContext, 0, "(");
                expect(enumerationContext, 1, IdentListContext.class);
                expect(enumerationContext, 2, ")");
                IdentListContext identListContext = (IdentListContext) enumerationContext.getChild(1);
                List<String> itemNames = new ArrayList<>();
                for (int i = 0; i < identListContext.getChildCount(); i++) {
                    ParseTree child = identListContext.getChild(i);
                    if (child instanceof IdentContext ident) {
                        itemNames.add(ident.getText());
                    } else if (child instanceof TerminalNode terminal) {
                        expectText(terminal, ",");
                    } else {
                        throw new UnexpectedTokenException(child);
                    }
                }
                EnumerationType enumerationType = new EnumerationType(loc(enumerationContext), scopeUnit, typeName);
                enumerationType.getElements().addAll(itemNames);
                return single(enumerationType);
            } else if (simpleType.getChild(0) instanceof SubrangeTypeContext subrangeType) {
                expect(subrangeType, 0, "[");
                expect(subrangeType, 1, ConstExpressionContext.class);
                expect(subrangeType, 2, "..");
                expect(subrangeType, 3, ConstExpressionContext.class);
                expect(subrangeType, 4, "]");
                
                ConstExpressionContext beginExpr = (ConstExpressionContext) subrangeType.getChild(1);
                ConstExpressionContext endExpr = (ConstExpressionContext) subrangeType.getChild(3);
                ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(scopeUnit);
                IExpression begin = processor.processConstantExpression(beginExpr);
                IExpression end = processor.processConstantExpression(endExpr);

                return single(new SubrangeType(loc(subrangeType), scopeUnit, typeName, begin, end));
            } else {
                throw new UnexpectedTokenException(simpleType.getChild(0));
            }
        } else if (typeContext.getChild(0) instanceof SetTypeContext setType) {
            expect(setType, 0, "SET");
            expect(setType, 1, "OF");
            if (setType.getChild(2) instanceof SimpleTypeContext simpleType) {
                if (simpleType.getChild(0) instanceof SubrangeTypeContext subrangeType) {
                    expect(subrangeType, 0, "[");
                    expect(subrangeType, 1, ConstExpressionContext.class);
                    expect(subrangeType, 2, "..");
                    expect(subrangeType, 3, ConstExpressionContext.class);
                    expect(subrangeType, 4, "]");
                    
                    ConstExpressionContext beginExpr = (ConstExpressionContext) subrangeType.getChild(1);
                    ConstExpressionContext endExpr = (ConstExpressionContext) subrangeType.getChild(3);
                    ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(scopeUnit);
                    IExpression begin = processor.processConstantExpression(beginExpr);
                    IExpression end = processor.processConstantExpression(endExpr);
                    
                    // Add the "_r" suffix because typeName is the SET type, not the underlying range type (which is unnamed)
                    RangeSetType rangeSetType = new RangeSetType(loc(subrangeType), scopeUnit, typeName + "_r", begin, end);
                    return single(rangeSetType);
                } else if (simpleType.getChild(0) instanceof QualidentContext qualidentContext) {
                    expectNbChild(qualidentContext, 1);
                    expect(qualidentContext, 0, IdentContext.class);
                    IdentContext identContext = (IdentContext) qualidentContext.getChild(0);
                    LiteralType enumType = new LiteralType(loc(identContext), scopeUnit, identContext.getText(), false);
                    return single(new EnumSetType(loc(identContext), scopeUnit, typeName, enumType));
                } else {
                    throw new UnexpectedTokenException(simpleType.getChild(0));
                }
            } else {
                throw new UnexpectedTokenException(setType.getChild(2));
            }
        } else if (typeContext.getChild(0) instanceof RecordTypeContext recordTypeContext) {
            expect(recordTypeContext, 0, "RECORD");
            expect(recordTypeContext, 1, FieldListSequenceContext.class);
            expect(recordTypeContext, 2, "END");
            
            FieldListSequenceContext fieldListSequence = (FieldListSequenceContext) recordTypeContext.getChild(1);
            List<VariableDefinition> recordItems = new ArrayList<>();
            List<IType> results = new ArrayList<>();
            processFieldListSequence(fieldListSequence, recordItems, results);
            
            RecordType recordType = new RecordType(loc(recordTypeContext), scopeUnit, typeName);
            recordType.getElements().addAll(recordItems);
            results.add(recordType);
            return results;
        } else if (typeContext.getChild(0) instanceof ArrayTypeContext arrayTypeContext) {
            int nbChild = arrayTypeContext.getChildCount();
            expect(arrayTypeContext, 0, "ARRAY");
            expect(arrayTypeContext, 1, SimpleTypeContext.class);
            expect(arrayTypeContext, nbChild - 2, "OF");
            expect(arrayTypeContext, nbChild - 1, Type_Context.class);
            
            // Range
            List<IExpression> lowerBounds = new ArrayList<>();
            List<IExpression> upperBounds = new ArrayList<>();
            for (int k = 1; k < nbChild - 2; k+= 2) {
                SimpleTypeContext simpleTypeContext = (SimpleTypeContext) arrayTypeContext.getChild(k);
                if (simpleTypeContext.getChild(0) instanceof SubrangeTypeContext subrangeType) {
                    expect(subrangeType, 0, "[");
                    expect(subrangeType, 1, ConstExpressionContext.class);
                    expect(subrangeType, 2, "..");
                    expect(subrangeType, 3, ConstExpressionContext.class);
                    expect(subrangeType, 4, "]");
                    
                    ConstExpressionContext beginExpr = (ConstExpressionContext) subrangeType.getChild(1);
                    ConstExpressionContext endExpr = (ConstExpressionContext) subrangeType.getChild(3);
                    ConstantDeclarationsProcessor processor = new ConstantDeclarationsProcessor(scopeUnit);
                    IExpression lowerBound = processor.processConstantExpression(beginExpr);
                    IExpression upperBound = processor.processConstantExpression(endExpr);
                    lowerBounds.add(lowerBound);
                    upperBounds.add(upperBound);
                } else {
                    throw new UnexpectedTokenException(simpleTypeContext.getChild(0));
                }
            }

            // Element type            
            Type_Context targetTypeContext = (Type_Context) arrayTypeContext.getChild(nbChild - 1);
            List<IType> results = processType(typeName, targetTypeContext);
            IType targetType = primary(results);
            
            IType result = targetType;
            assert lowerBounds.size() == upperBounds.size();
            for (int k = lowerBounds.size() - 1; k >= 0; k--) {
                result = new ArrayType(loc(targetTypeContext), scopeUnit, k== 0 ? typeName : "<anonymous>",
                        lowerBounds.get(k), upperBounds.get(k), result);
            }
            results.add(result);
            
            return results;
        } else if (typeContext.getChild(0) instanceof PointerTypeContext pointerType) {
            expect(pointerType, 0, "POINTER");
            expect(pointerType, 1, "TO");
            expect(pointerType, 2, Type_Context.class);
            Type_Context targetTypeContext = (Type_Context) pointerType.getChild(2);
            List<IType> results = processType(typeName, targetTypeContext);
            IType targetType = primary(results);
            results.add(new PointerType(loc(targetTypeContext), scopeUnit, typeName, targetType));
            return results;
        } else if (typeContext.getChild(0) instanceof ProcedureTypeContext procedureTypeContext) {
            expect(procedureTypeContext, 0, "PROCEDURE");
            expect(procedureTypeContext, 1, FormalTypeListContext.class);

            FormalTypeListContext formalTypeList = (FormalTypeListContext) procedureTypeContext.getChild(1);
            int nbChildren = formalTypeList.getChildCount();
            boolean hasReturnType = formalTypeList.getChildCount() > 2 
                    && formalTypeList.getChild(nbChildren - 2).getText().equals(":");
            
            List<IType> argumentTypes = new ArrayList<>();
            BitSet varArguments = new BitSet();
            expect(formalTypeList, 0, "(");
            int closeParentheseIndex;
            if (hasReturnType) {
                expect(formalTypeList, nbChildren - 3, ")");
                expect(formalTypeList, nbChildren - 2, ":");
                expect(formalTypeList, nbChildren - 1, QualidentContext.class);
                closeParentheseIndex = nbChildren - 3;
            } else {
                expect(formalTypeList, nbChildren - 1, ")");
                closeParentheseIndex = nbChildren - 1;
            }
            
            // Process argument types
            for (int i = 1; i < closeParentheseIndex; i++) {
                if (formalTypeList.getChild(i) instanceof TerminalNode terminal) {
                    String text = terminal.getText();
                    if (text.equals("VAR"))
                        varArguments.set(argumentTypes.size());
                    else
                        expectText(terminal, ",");
                } else if (formalTypeList.getChild(i) instanceof FormalTypeContext formalTypeContext) {
                    expect(formalTypeContext, 0, QualidentContext.class);
                    QualidentContext paramQualident = (QualidentContext) formalTypeContext.getChild(0);
                    if (paramQualident.getChild(0) instanceof IdentContext paramTypeIdent) {
                        String paramIdentName = paramTypeIdent.getText();
                        IType paramType = new LiteralType(loc(paramTypeIdent), scopeUnit, paramIdentName, false);
                        argumentTypes.add(paramType);
                    } else {
                        throw new UnexpectedTokenException(paramQualident.getChild(0));
                    }
                } else {
                    throw new UnexpectedTokenException(formalTypeList.getChild(i));
                }
            }

            // Process return type
            IType returnType = null;
            if (hasReturnType) {
                QualidentContext returnQualident = (QualidentContext) formalTypeList.getChild(nbChildren - 1);
                if (returnQualident.getChild(0) instanceof IdentContext returnTypeIdent) {
                    String returnIdentName = returnTypeIdent.getText();
                    returnType = new LiteralType(loc(returnTypeIdent), scopeUnit, returnIdentName, false);
                } else {
                    throw new UnexpectedTokenException(returnQualident.getChild(0));
                }
            }
            IType result = new ProcedureType(loc(procedureTypeContext), scopeUnit, typeName, argumentTypes, varArguments, returnType);
            return single(result);
        } else {
            throw new UnexpectedTokenException(typeContext.getChild(0));
        }
    }

    private void processFieldListSequence(FieldListSequenceContext fieldListSequence, 
            List<VariableDefinition> result, List<IType> nestedTypes) {
        for (ParseTree child : fieldListSequence.children) {
            if (child instanceof TerminalNodeImpl terminal) {
                expectText(terminal, ";");
            } else if (child instanceof FieldListContext fieldList) {
                if (fieldList.getChild(0) instanceof IdentListContext identList) {
                    expect(fieldList, 1, ":");
                    expect(fieldList, 2, Type_Context.class);
                    
                    List<String> identNames = new ArrayList<>();
                    for (ParseTree identItem : identList.children) {
                        if (identItem instanceof IdentContext identMember) {
                            String name = identMember.getText();
                            identNames.add(name);
                        } else if (identItem instanceof TerminalNodeImpl terminal) {
                            expectText(terminal, ",");
                        } else {
                            throw new UnexpectedTokenException(identItem);
                        }
                    }
                    String typeName = "";
                    for (String identName : identNames)
                        typeName += "_" + StringUtils.toPascalCase(identName);
                    Type_Context typeContext0 = (Type_Context) fieldList.getChild(2);
                    List<IType> types = processType(typeName, typeContext0);
                    for (IType type : types) {
                        if (Types.requiresExplicitDeclaration(type))
                            nestedTypes.add(type);
                    }
                    IType fieldType = primary(types);
                    for (String identName : identNames) {
                        VariableDefinition variableDefinition = new VariableDefinition(loc(identList), scopeUnit, identName, fieldType);
                        result.add(variableDefinition);
                    }
                } else if (fieldList.getChild(0) instanceof TerminalNodeImpl terminalNode) {
                    String terminal = terminalNode.getText();
                    if (terminal.equals("CASE")) {
                        expect(fieldList, 1, IdentContext.class); // var
                        expect(fieldList, 2, ":");
                        expect(fieldList, 3, QualidentContext.class); // type
                        expect(fieldList, 4, "OF");
                        String caseName = fieldList.getChild(1).getText();
                        QualidentContext qualidentContext = (QualidentContext) fieldList.getChild(3);
                        IdentContext identContext = (IdentContext) qualidentContext.getChild(0);
                        LiteralType caseType = new LiteralType(loc(identContext), scopeUnit, identContext.getText(), false);
                        
                        // Add CASE variable as a regular field
                        VariableDefinition caseVariable = new VariableDefinition(loc(identContext), null, caseName, caseType);
                        result.add(caseVariable);
                        
                        // Process variants
                        CaseVariantType caseVariantType = new CaseVariantType(loc(identContext), scopeUnit, caseName);
                        for (int i = 5; i < fieldList.getChildCount(); i++) {
                            if (fieldList.getChild(i) instanceof TerminalNodeImpl sep) {
                                Set<String> allowedSeps = Set.of("|", "ELSE", "END");
                                assert allowedSeps.contains(sep.getText()) : sep.getText();
                            } else if (fieldList.getChild(i) instanceof VariantContext variant) {
                                expect(variant, 0, CaseLabelListContext.class);
                                expect(variant, 1, ":");
                                expect(variant, 2, FieldListSequenceContext.class);
                                
                                String label = variant.getChild(0).getText();
                                FieldListSequenceContext fieldListSequence1 = (FieldListSequenceContext) variant.getChild(2);
                                List<VariableDefinition> variableDefinitions = new ArrayList<>();
                                processFieldListSequence(fieldListSequence1, variableDefinitions, nestedTypes);
                                caseVariantType.getVariants().put(new ConstantLiteral(loc(variant.getChild(0)), label), 
                                        variableDefinitions);
                            } else if (fieldList.getChild(i) instanceof FieldListSequenceContext fieldListSequence1) {
                                // Can only happen for a label with no item
                                expectNbChild(fieldListSequence1, 1);
                                expect(fieldListSequence1, 0, FieldListContext.class);
                                FieldListContext fieldList1 = (FieldListContext) fieldListSequence1.getChild(0);
                                expectNbChild(fieldList1, 0);
                            } else {
                                throw new UnexpectedTokenException(fieldList.getChild(i));
                            }
                        }
                        VariableDefinition variableDefinition = new VariableDefinition(loc(fieldList), null, caseName, caseVariantType);
                        result.add(variableDefinition);
                    } else {
                        throw new UnexpectedTokenException(terminalNode);
                    }
                } else if (fieldList.getChild(0) == null) {
                    /*
                     * Seems we got an extra ";" after a case. Just continue...
                     */
                } else {
                    throw new UnexpectedTokenException(fieldList.getChild(0));
                }
            }
        }
    }

    private Set<String> allocatedNames = new HashSet<>();

    public String allocateName(String name) {
        int index = 1;
        String result = name + "1";
        while (allocatedNames.contains(result)) {
            index++;
            result = name + index;
        }
        allocatedNames.add(result);
        return result;
    }
    
    public void addNestedTypes(List<IType> types, List<TypeDefinition> target) {
        for (IType type : types) {
            if (type instanceof INamedType namedType) {
                String typeName = namedType.getName();
                if (typeName.startsWith("<")) {
                    String baseName = type.getClass().getSimpleName();
                    if (baseName.endsWith("Type"))
                        baseName = baseName.substring(0, baseName.length() - "Type".length());
                    typeName = "_" + allocateName(baseName);
                    namedType.setName(typeName);
                }
                TypeDefinition typeDefinition = new TypeDefinition(((SourceElement) type).getSourceLocation(),
                        type.getDeclaringScope(), typeName, type);
                target.add(typeDefinition);
            } else {
                throw new CompilerException(type, "Cannot create definition for nested unnamed type {0}", type);
            }
        }
    }

}
