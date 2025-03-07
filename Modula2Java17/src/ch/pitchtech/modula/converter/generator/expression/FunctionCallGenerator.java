package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.statement.MethodCallGenerator;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;

public class FunctionCallGenerator extends MethodCallGenerator {
    
    private final FunctionCall functionCall;
    private final IType expectedReturnType;

    
    public FunctionCallGenerator(IHasScope scopeUnit, FunctionCall functionCall, IType expectedReturnType) {
        super(scopeUnit, functionCall);
        this.functionCall = functionCall;
        this.expectedReturnType = expectedReturnType;
    }

    @Override
    public void generate(ResultContext result) {
        super.generate(functionCall.getFunctionName(), functionCall.getArguments(), result);
    }

    @Override
    protected void handleBuiltInProcedure(BuiltInProcedure builtInProc, ResultContext result) {
        new BuiltInFunctionCallGenerator(scopeUnit, functionCall, builtInProc).generate(result, expectedReturnType);
    }

}
