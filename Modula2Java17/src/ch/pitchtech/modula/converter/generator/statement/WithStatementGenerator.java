package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.type.RecordTypeGenerator;
import ch.pitchtech.modula.converter.model.expression.Dereference;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.WithStatement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.utils.StringUtils;


public class WithStatementGenerator extends Generator {
    
    private final WithStatement withStatement;

    
    public WithStatementGenerator(IHasScope scopeUnit, WithStatement withStatement) {
        super(scopeUnit, withStatement);
        this.withStatement = withStatement;
    }

    @Override
    public void generate(ResultContext result) {
        IType type = result.resolveType(withStatement.getExpression());
        if (!(type instanceof RecordType recordType))
            throw new CompilationException(withStatement, "Not a record type '{0}' : {1}", 
                    withStatement.getExpression(), type);

        ResultContext exprResult = result.subContext();
        Expressions.getGenerator(scopeUnit, withStatement.getExpression()).generate(exprResult);
        if (isSimple(withStatement)) {
            generateSimple(recordType, result, exprResult);
        } else {
            generateWithVariable(recordType, result, exprResult);
        }
    }
    
    /**
     * Whether this WITH statement's expression is simple and will not use a helper variable when generated.
     * <p>
     * When <tt>false</tt>, the expression might have side-effect and should only be evaluated once.
     * In that case it will be stored in a local variable given by {@link WithStatement#getLocalVariableName()}.
     */
    public static boolean isSimple(WithStatement withStatement) {
        IExpression expression = withStatement.getExpression();
        if (expression instanceof Identifier)
            return true;
        if (expression instanceof Dereference dereference && dereference.getPointer() instanceof Identifier)
            return true; // WITH x^
        return false;
    }
    
    /**
     * Used when the WITH statement's expression resolves to a simple variable.
     * <p>
     * In that case we do not copy the expression in a new variable, but use the simple variable instead
     */
    private void generateSimple(RecordType recordType, ResultContext result, ResultContext exprResult) {
        String variableName = exprResult.toString();
        withStatement.setLocalVariableName(variableName);
        
        ResultContext typeResult = result.subContext();
        new RecordTypeGenerator(scopeUnit, recordType).generate(typeResult);
        
        if (result.getCompilerOptions().isMarkSimplifiedWith())
            result.writeLine("// WITH " + exprResult.toString() + " {");
        result.pushScope(withStatement.getLocalScope());
        for (IStatement statement : withStatement.getStatements()) {
            Statements.generate(withStatement, statement, result);
        }
        result.popScope();
        if (result.getCompilerOptions().isMarkSimplifiedWith())
            result.writeLine("// }");
    }

    private void generateWithVariable(RecordType recordType, ResultContext result, ResultContext exprResult) {
        // Allocate a variable
        String variableName = "_" + StringUtils.toCamelCase(recordType.getName());
        variableName = result.allocateName(variableName, false);
        withStatement.setLocalVariableName(variableName);
        
        ResultContext typeResult = result.subContext();
        new RecordTypeGenerator(scopeUnit, recordType).generate(typeResult);
        
        result.writeLine("{ // WITH");
        result.incIndent();
        result.writeLine(typeResult.toString() + " " + variableName + " = " + exprResult.toString() + ";");
        result.pushScope(withStatement.getLocalScope());
        for (IStatement statement : withStatement.getStatements()) {
            Statements.generate(withStatement, statement, result);
        }
        result.decIndent();
        result.popScope();
        result.freeName(variableName);
        result.writeLine("}");
    }

}
