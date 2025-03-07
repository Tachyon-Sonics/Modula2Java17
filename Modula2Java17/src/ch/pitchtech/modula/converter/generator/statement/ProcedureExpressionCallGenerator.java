package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.ProcedureExpressionCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

/**
 * Call to an expression of procedure type. The typical case is "P(...)" where
 * "P" is a variable of procedure type. However, arbitrary expressions (like "obj^.P(...)"
 * for example) are also possible.
 */
public class ProcedureExpressionCallGenerator extends Generator {
    
    private final ProcedureExpressionCall procedureExpressionCall;

    
    public ProcedureExpressionCallGenerator(IHasScope scopeUnit, ProcedureExpressionCall procedureExpressionCall) {
        super(scopeUnit, procedureExpressionCall);
        this.procedureExpressionCall = procedureExpressionCall;
    }

    @Override
    public void generate(ResultContext result) {
        IExpression procExpr = procedureExpressionCall.getProcedureExpr();
        IType exprType = result.resolveProcedureType(procExpr, scopeUnit);
        if (!(exprType instanceof ProcedureType procedureType))
            throw new CompilationException(procedureExpressionCall, "Expression is not of PROCEDURE type: {0} is of type {1}",
                   procExpr, exprType);
        
        result.writeIndent();
        Expressions.getGenerator(scopeUnit, procExpr).generate(result);
        IType procType = result.resolveType(procExpr);
        if (procType.isBuiltInType(BuiltInType.PROC)) {
            result.write(".run(");
        } else {
            result.write(".invoke("); 
        }
        for (int i = 0; i < procedureExpressionCall.getArguments().size(); i++) {
            IExpression argument = procedureExpressionCall.getArguments().get(i);
            if (i > 0)
                result.write(", ");
            IType formalType = result.resolveType(procedureType.getArgumentTypes().get(i));
            boolean isVar = procedureType.isVarArg(i);
            MethodCallGenerator.writeActualArgument(result, scopeUnit, argument, isVar, formalType, isVar);
        }
        result.write(");");
        result.writeLn();
    }

}
