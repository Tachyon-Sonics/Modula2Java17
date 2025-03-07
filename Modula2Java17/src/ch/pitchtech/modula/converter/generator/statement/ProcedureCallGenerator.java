package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;


public class ProcedureCallGenerator extends MethodCallGenerator {
    
    private final ProcedureCall procedureCall;

    
    public ProcedureCallGenerator(IHasScope scopeUnit, ProcedureCall procedureCall) {
        super(scopeUnit, procedureCall);
        this.procedureCall = procedureCall;
    }

    @Override
    public void generate(ResultContext result) {
        ResultContext callResult = result.subContext();
        boolean builtIn = super.generate(procedureCall.getProcedureName(), procedureCall.getArguments(), callResult);
        if (!builtIn)
            result.writeIndent();
        result.write(callResult);
        if (!builtIn) {
            result.write(";");
            result.writeLn();
        }
    }

    @Override
    protected void handleBuiltInProcedure(BuiltInProcedure builtInProc, ResultContext result) {
        new BuiltInProcedureCallGenerator(scopeUnit, procedureCall, builtInProc).generate(result);
    }

}
