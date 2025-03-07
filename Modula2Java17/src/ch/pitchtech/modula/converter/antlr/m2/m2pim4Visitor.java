package ch.pitchtech.modula.converter.antlr.m2;

// Generated from m2pim4.g4 by ANTLR 4.12.0
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link m2pim4Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface m2pim4Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#ident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdent(m2pim4Parser.IdentContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(m2pim4Parser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#integer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger(m2pim4Parser.IntegerContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#real}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReal(m2pim4Parser.RealContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#scaleFactor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitScaleFactor(m2pim4Parser.ScaleFactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#hexDigit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHexDigit(m2pim4Parser.HexDigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#digit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDigit(m2pim4Parser.DigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#octalDigit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOctalDigit(m2pim4Parser.OctalDigitContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#string}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString(m2pim4Parser.StringContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#qualident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualident(m2pim4Parser.QualidentContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#constantDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstantDeclaration(m2pim4Parser.ConstantDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#constExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstExpression(m2pim4Parser.ConstExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#relation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelation(m2pim4Parser.RelationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#simpleConstExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleConstExpr(m2pim4Parser.SimpleConstExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#addOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddOperator(m2pim4Parser.AddOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#constTerm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstTerm(m2pim4Parser.ConstTermContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#mulOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulOperator(m2pim4Parser.MulOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#constFactor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstFactor(m2pim4Parser.ConstFactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#setOrQualident}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetOrQualident(m2pim4Parser.SetOrQualidentContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#set_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_(m2pim4Parser.Set_Context ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#element}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElement(m2pim4Parser.ElementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#typeDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDeclaration(m2pim4Parser.TypeDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#type_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_(m2pim4Parser.Type_Context ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#simpleType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleType(m2pim4Parser.SimpleTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#enumeration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnumeration(m2pim4Parser.EnumerationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#identList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentList(m2pim4Parser.IdentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#subrangeType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSubrangeType(m2pim4Parser.SubrangeTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#arrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayType(m2pim4Parser.ArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#recordType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRecordType(m2pim4Parser.RecordTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#fieldListSequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldListSequence(m2pim4Parser.FieldListSequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#fieldList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldList(m2pim4Parser.FieldListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#variant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariant(m2pim4Parser.VariantContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#caseLabelList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseLabelList(m2pim4Parser.CaseLabelListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#caseLabels}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseLabels(m2pim4Parser.CaseLabelsContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#setType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetType(m2pim4Parser.SetTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#pointerType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPointerType(m2pim4Parser.PointerTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#procedureType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureType(m2pim4Parser.ProcedureTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#formalTypeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalTypeList(m2pim4Parser.FormalTypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#variableDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaration(m2pim4Parser.VariableDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#designator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesignator(m2pim4Parser.DesignatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#designatorTail}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDesignatorTail(m2pim4Parser.DesignatorTailContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#expList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpList(m2pim4Parser.ExpListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(m2pim4Parser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#simpleExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpression(m2pim4Parser.SimpleExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(m2pim4Parser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#factor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFactor(m2pim4Parser.FactorContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#setOrDesignatorOrProcCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSetOrDesignatorOrProcCall(m2pim4Parser.SetOrDesignatorOrProcCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#actualParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActualParameters(m2pim4Parser.ActualParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(m2pim4Parser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#assignmentOrProcCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentOrProcCall(m2pim4Parser.AssignmentOrProcCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#statementSequence}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatementSequence(m2pim4Parser.StatementSequenceContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(m2pim4Parser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#caseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCaseStatement(m2pim4Parser.CaseStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#ccase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCcase(m2pim4Parser.CcaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#whileStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStatement(m2pim4Parser.WhileStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#repeatStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRepeatStatement(m2pim4Parser.RepeatStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#forStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStatement(m2pim4Parser.ForStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#loopStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopStatement(m2pim4Parser.LoopStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#withStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWithStatement(m2pim4Parser.WithStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#procedureDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureDeclaration(m2pim4Parser.ProcedureDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#procedureHeading}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProcedureHeading(m2pim4Parser.ProcedureHeadingContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(m2pim4Parser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(m2pim4Parser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(m2pim4Parser.FormalParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#fpSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFpSection(m2pim4Parser.FpSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#formalType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalType(m2pim4Parser.FormalTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#moduleDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleDeclaration(m2pim4Parser.ModuleDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#priority}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPriority(m2pim4Parser.PriorityContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#exportList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExportList(m2pim4Parser.ExportListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#importList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImportList(m2pim4Parser.ImportListContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#definitionModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinitionModule(m2pim4Parser.DefinitionModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#definition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefinition(m2pim4Parser.DefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#programModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramModule(m2pim4Parser.ProgramModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link m2pim4Parser#compilationUnit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCompilationUnit(m2pim4Parser.CompilationUnitContext ctx);
}