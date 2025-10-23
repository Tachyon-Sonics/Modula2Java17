// Generated from src/ch/pitchtech/modula/converter/antlr/m2/m2pim4.g4 by ANTLR 4.12.0
package ch.pitchtech.modula.converter.antlr.m2;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link m2pim4Parser}.
 */
public interface m2pim4Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(m2pim4Parser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(m2pim4Parser.IdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(m2pim4Parser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(m2pim4Parser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#integer}.
	 * @param ctx the parse tree
	 */
	void enterInteger(m2pim4Parser.IntegerContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#integer}.
	 * @param ctx the parse tree
	 */
	void exitInteger(m2pim4Parser.IntegerContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#real}.
	 * @param ctx the parse tree
	 */
	void enterReal(m2pim4Parser.RealContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#real}.
	 * @param ctx the parse tree
	 */
	void exitReal(m2pim4Parser.RealContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#scaleFactor}.
	 * @param ctx the parse tree
	 */
	void enterScaleFactor(m2pim4Parser.ScaleFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#scaleFactor}.
	 * @param ctx the parse tree
	 */
	void exitScaleFactor(m2pim4Parser.ScaleFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#hexDigit}.
	 * @param ctx the parse tree
	 */
	void enterHexDigit(m2pim4Parser.HexDigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#hexDigit}.
	 * @param ctx the parse tree
	 */
	void exitHexDigit(m2pim4Parser.HexDigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#digit}.
	 * @param ctx the parse tree
	 */
	void enterDigit(m2pim4Parser.DigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#digit}.
	 * @param ctx the parse tree
	 */
	void exitDigit(m2pim4Parser.DigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#octalDigit}.
	 * @param ctx the parse tree
	 */
	void enterOctalDigit(m2pim4Parser.OctalDigitContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#octalDigit}.
	 * @param ctx the parse tree
	 */
	void exitOctalDigit(m2pim4Parser.OctalDigitContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#string}.
	 * @param ctx the parse tree
	 */
	void enterString(m2pim4Parser.StringContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#string}.
	 * @param ctx the parse tree
	 */
	void exitString(m2pim4Parser.StringContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#qualident}.
	 * @param ctx the parse tree
	 */
	void enterQualident(m2pim4Parser.QualidentContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#qualident}.
	 * @param ctx the parse tree
	 */
	void exitQualident(m2pim4Parser.QualidentContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstantDeclaration(m2pim4Parser.ConstantDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstantDeclaration(m2pim4Parser.ConstantDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#constExpression}.
	 * @param ctx the parse tree
	 */
	void enterConstExpression(m2pim4Parser.ConstExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#constExpression}.
	 * @param ctx the parse tree
	 */
	void exitConstExpression(m2pim4Parser.ConstExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#relation}.
	 * @param ctx the parse tree
	 */
	void enterRelation(m2pim4Parser.RelationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#relation}.
	 * @param ctx the parse tree
	 */
	void exitRelation(m2pim4Parser.RelationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#simpleConstExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleConstExpr(m2pim4Parser.SimpleConstExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#simpleConstExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleConstExpr(m2pim4Parser.SimpleConstExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#addOperator}.
	 * @param ctx the parse tree
	 */
	void enterAddOperator(m2pim4Parser.AddOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#addOperator}.
	 * @param ctx the parse tree
	 */
	void exitAddOperator(m2pim4Parser.AddOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#constTerm}.
	 * @param ctx the parse tree
	 */
	void enterConstTerm(m2pim4Parser.ConstTermContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#constTerm}.
	 * @param ctx the parse tree
	 */
	void exitConstTerm(m2pim4Parser.ConstTermContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#mulOperator}.
	 * @param ctx the parse tree
	 */
	void enterMulOperator(m2pim4Parser.MulOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#mulOperator}.
	 * @param ctx the parse tree
	 */
	void exitMulOperator(m2pim4Parser.MulOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#constFactor}.
	 * @param ctx the parse tree
	 */
	void enterConstFactor(m2pim4Parser.ConstFactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#constFactor}.
	 * @param ctx the parse tree
	 */
	void exitConstFactor(m2pim4Parser.ConstFactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#setOrQualident}.
	 * @param ctx the parse tree
	 */
	void enterSetOrQualident(m2pim4Parser.SetOrQualidentContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#setOrQualident}.
	 * @param ctx the parse tree
	 */
	void exitSetOrQualident(m2pim4Parser.SetOrQualidentContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#set_}.
	 * @param ctx the parse tree
	 */
	void enterSet_(m2pim4Parser.Set_Context ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#set_}.
	 * @param ctx the parse tree
	 */
	void exitSet_(m2pim4Parser.Set_Context ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#element}.
	 * @param ctx the parse tree
	 */
	void enterElement(m2pim4Parser.ElementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#element}.
	 * @param ctx the parse tree
	 */
	void exitElement(m2pim4Parser.ElementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterTypeDeclaration(m2pim4Parser.TypeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitTypeDeclaration(m2pim4Parser.TypeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#type_}.
	 * @param ctx the parse tree
	 */
	void enterType_(m2pim4Parser.Type_Context ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#type_}.
	 * @param ctx the parse tree
	 */
	void exitType_(m2pim4Parser.Type_Context ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#simpleType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleType(m2pim4Parser.SimpleTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#simpleType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleType(m2pim4Parser.SimpleTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#enumeration}.
	 * @param ctx the parse tree
	 */
	void enterEnumeration(m2pim4Parser.EnumerationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#enumeration}.
	 * @param ctx the parse tree
	 */
	void exitEnumeration(m2pim4Parser.EnumerationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#identList}.
	 * @param ctx the parse tree
	 */
	void enterIdentList(m2pim4Parser.IdentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#identList}.
	 * @param ctx the parse tree
	 */
	void exitIdentList(m2pim4Parser.IdentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#subrangeType}.
	 * @param ctx the parse tree
	 */
	void enterSubrangeType(m2pim4Parser.SubrangeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#subrangeType}.
	 * @param ctx the parse tree
	 */
	void exitSubrangeType(m2pim4Parser.SubrangeTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(m2pim4Parser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#arrayType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(m2pim4Parser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#recordType}.
	 * @param ctx the parse tree
	 */
	void enterRecordType(m2pim4Parser.RecordTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#recordType}.
	 * @param ctx the parse tree
	 */
	void exitRecordType(m2pim4Parser.RecordTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#fieldListSequence}.
	 * @param ctx the parse tree
	 */
	void enterFieldListSequence(m2pim4Parser.FieldListSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#fieldListSequence}.
	 * @param ctx the parse tree
	 */
	void exitFieldListSequence(m2pim4Parser.FieldListSequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#fieldList}.
	 * @param ctx the parse tree
	 */
	void enterFieldList(m2pim4Parser.FieldListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#fieldList}.
	 * @param ctx the parse tree
	 */
	void exitFieldList(m2pim4Parser.FieldListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#variant}.
	 * @param ctx the parse tree
	 */
	void enterVariant(m2pim4Parser.VariantContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#variant}.
	 * @param ctx the parse tree
	 */
	void exitVariant(m2pim4Parser.VariantContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#caseLabelList}.
	 * @param ctx the parse tree
	 */
	void enterCaseLabelList(m2pim4Parser.CaseLabelListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#caseLabelList}.
	 * @param ctx the parse tree
	 */
	void exitCaseLabelList(m2pim4Parser.CaseLabelListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#caseLabels}.
	 * @param ctx the parse tree
	 */
	void enterCaseLabels(m2pim4Parser.CaseLabelsContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#caseLabels}.
	 * @param ctx the parse tree
	 */
	void exitCaseLabels(m2pim4Parser.CaseLabelsContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#setType}.
	 * @param ctx the parse tree
	 */
	void enterSetType(m2pim4Parser.SetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#setType}.
	 * @param ctx the parse tree
	 */
	void exitSetType(m2pim4Parser.SetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#pointerType}.
	 * @param ctx the parse tree
	 */
	void enterPointerType(m2pim4Parser.PointerTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#pointerType}.
	 * @param ctx the parse tree
	 */
	void exitPointerType(m2pim4Parser.PointerTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#procedureType}.
	 * @param ctx the parse tree
	 */
	void enterProcedureType(m2pim4Parser.ProcedureTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#procedureType}.
	 * @param ctx the parse tree
	 */
	void exitProcedureType(m2pim4Parser.ProcedureTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#formalTypeList}.
	 * @param ctx the parse tree
	 */
	void enterFormalTypeList(m2pim4Parser.FormalTypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#formalTypeList}.
	 * @param ctx the parse tree
	 */
	void exitFormalTypeList(m2pim4Parser.FormalTypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(m2pim4Parser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(m2pim4Parser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#designator}.
	 * @param ctx the parse tree
	 */
	void enterDesignator(m2pim4Parser.DesignatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#designator}.
	 * @param ctx the parse tree
	 */
	void exitDesignator(m2pim4Parser.DesignatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#designatorTail}.
	 * @param ctx the parse tree
	 */
	void enterDesignatorTail(m2pim4Parser.DesignatorTailContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#designatorTail}.
	 * @param ctx the parse tree
	 */
	void exitDesignatorTail(m2pim4Parser.DesignatorTailContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#expList}.
	 * @param ctx the parse tree
	 */
	void enterExpList(m2pim4Parser.ExpListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#expList}.
	 * @param ctx the parse tree
	 */
	void exitExpList(m2pim4Parser.ExpListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(m2pim4Parser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(m2pim4Parser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpression(m2pim4Parser.SimpleExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#simpleExpression}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpression(m2pim4Parser.SimpleExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(m2pim4Parser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(m2pim4Parser.TermContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#factor}.
	 * @param ctx the parse tree
	 */
	void enterFactor(m2pim4Parser.FactorContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#factor}.
	 * @param ctx the parse tree
	 */
	void exitFactor(m2pim4Parser.FactorContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#setOrDesignatorOrProcCall}.
	 * @param ctx the parse tree
	 */
	void enterSetOrDesignatorOrProcCall(m2pim4Parser.SetOrDesignatorOrProcCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#setOrDesignatorOrProcCall}.
	 * @param ctx the parse tree
	 */
	void exitSetOrDesignatorOrProcCall(m2pim4Parser.SetOrDesignatorOrProcCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#actualParameters}.
	 * @param ctx the parse tree
	 */
	void enterActualParameters(m2pim4Parser.ActualParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#actualParameters}.
	 * @param ctx the parse tree
	 */
	void exitActualParameters(m2pim4Parser.ActualParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(m2pim4Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(m2pim4Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#assignmentOrProcCall}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentOrProcCall(m2pim4Parser.AssignmentOrProcCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#assignmentOrProcCall}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentOrProcCall(m2pim4Parser.AssignmentOrProcCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#statementSequence}.
	 * @param ctx the parse tree
	 */
	void enterStatementSequence(m2pim4Parser.StatementSequenceContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#statementSequence}.
	 * @param ctx the parse tree
	 */
	void exitStatementSequence(m2pim4Parser.StatementSequenceContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(m2pim4Parser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(m2pim4Parser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void enterCaseStatement(m2pim4Parser.CaseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#caseStatement}.
	 * @param ctx the parse tree
	 */
	void exitCaseStatement(m2pim4Parser.CaseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#ccase}.
	 * @param ctx the parse tree
	 */
	void enterCcase(m2pim4Parser.CcaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#ccase}.
	 * @param ctx the parse tree
	 */
	void exitCcase(m2pim4Parser.CcaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStatement(m2pim4Parser.WhileStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#whileStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStatement(m2pim4Parser.WhileStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#repeatStatement}.
	 * @param ctx the parse tree
	 */
	void enterRepeatStatement(m2pim4Parser.RepeatStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#repeatStatement}.
	 * @param ctx the parse tree
	 */
	void exitRepeatStatement(m2pim4Parser.RepeatStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void enterForStatement(m2pim4Parser.ForStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#forStatement}.
	 * @param ctx the parse tree
	 */
	void exitForStatement(m2pim4Parser.ForStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement(m2pim4Parser.LoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement(m2pim4Parser.LoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#withStatement}.
	 * @param ctx the parse tree
	 */
	void enterWithStatement(m2pim4Parser.WithStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#withStatement}.
	 * @param ctx the parse tree
	 */
	void exitWithStatement(m2pim4Parser.WithStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#procedureDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterProcedureDeclaration(m2pim4Parser.ProcedureDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#procedureDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitProcedureDeclaration(m2pim4Parser.ProcedureDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#procedureHeading}.
	 * @param ctx the parse tree
	 */
	void enterProcedureHeading(m2pim4Parser.ProcedureHeadingContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#procedureHeading}.
	 * @param ctx the parse tree
	 */
	void exitProcedureHeading(m2pim4Parser.ProcedureHeadingContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(m2pim4Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(m2pim4Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(m2pim4Parser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(m2pim4Parser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(m2pim4Parser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(m2pim4Parser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#fpSection}.
	 * @param ctx the parse tree
	 */
	void enterFpSection(m2pim4Parser.FpSectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#fpSection}.
	 * @param ctx the parse tree
	 */
	void exitFpSection(m2pim4Parser.FpSectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#formalType}.
	 * @param ctx the parse tree
	 */
	void enterFormalType(m2pim4Parser.FormalTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#formalType}.
	 * @param ctx the parse tree
	 */
	void exitFormalType(m2pim4Parser.FormalTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterModuleDeclaration(m2pim4Parser.ModuleDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#moduleDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitModuleDeclaration(m2pim4Parser.ModuleDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#priority}.
	 * @param ctx the parse tree
	 */
	void enterPriority(m2pim4Parser.PriorityContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#priority}.
	 * @param ctx the parse tree
	 */
	void exitPriority(m2pim4Parser.PriorityContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#exportList}.
	 * @param ctx the parse tree
	 */
	void enterExportList(m2pim4Parser.ExportListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#exportList}.
	 * @param ctx the parse tree
	 */
	void exitExportList(m2pim4Parser.ExportListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#importList}.
	 * @param ctx the parse tree
	 */
	void enterImportList(m2pim4Parser.ImportListContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#importList}.
	 * @param ctx the parse tree
	 */
	void exitImportList(m2pim4Parser.ImportListContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#definitionModule}.
	 * @param ctx the parse tree
	 */
	void enterDefinitionModule(m2pim4Parser.DefinitionModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#definitionModule}.
	 * @param ctx the parse tree
	 */
	void exitDefinitionModule(m2pim4Parser.DefinitionModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#definition}.
	 * @param ctx the parse tree
	 */
	void enterDefinition(m2pim4Parser.DefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#definition}.
	 * @param ctx the parse tree
	 */
	void exitDefinition(m2pim4Parser.DefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#programModule}.
	 * @param ctx the parse tree
	 */
	void enterProgramModule(m2pim4Parser.ProgramModuleContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#programModule}.
	 * @param ctx the parse tree
	 */
	void exitProgramModule(m2pim4Parser.ProgramModuleContext ctx);
	/**
	 * Enter a parse tree produced by {@link m2pim4Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void enterCompilationUnit(m2pim4Parser.CompilationUnitContext ctx);
	/**
	 * Exit a parse tree produced by {@link m2pim4Parser#compilationUnit}.
	 * @param ctx the parse tree
	 */
	void exitCompilationUnit(m2pim4Parser.CompilationUnitContext ctx);
}