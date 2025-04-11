package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.ReturnStatement;
import ch.pitchtech.modula.converter.model.type.IType;


public class ReturnStatementGenerator extends Generator {
    
    private final ReturnStatement returnStatement;

    
    public ReturnStatementGenerator(IHasScope scopeUnit, ReturnStatement returnStatement) {
        super(scopeUnit, returnStatement);
        this.returnStatement = returnStatement;
    }

    @Override
    public void generate(ResultContext result) {
        result.writeIndent();
        result.write("return");
        if (returnStatement.getExpression() != null) {
            IType targetType0 = returnStatement.getProcedureImplementation().getReturnType();
            if (targetType0 == null)
                throw new CompilationException(this, "Cannot return an expression in a PROCEDURE that does not return anything");
            IType targetType = result.resolveType(targetType0);
            IType returnType = result.resolveType(returnStatement.getExpression());
            
            result.write(" ");
            AssignmentGenerator.writeValueWithProperCast(result, scopeUnit, targetType, returnStatement.getExpression(), 
                    returnType, true, false);
        }
        result.write(";");
        result.writeLn();
    }

}
