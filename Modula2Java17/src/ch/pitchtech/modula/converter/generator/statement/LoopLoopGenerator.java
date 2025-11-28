package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.LoopLoop;


public class LoopLoopGenerator extends Generator {
    
    private final LoopLoop loopLoop;

    
    public LoopLoopGenerator(IHasScope scopeUnit, LoopLoop loopLoop) {
        super(scopeUnit, loopLoop);
        this.loopLoop = loopLoop;
    }

    @Override
    public void generate(ResultContext result) {
        result.writeLine("while (true) {");
        result.incIndent();
        for (IStatement statement : loopLoop.getStatements())
            Statements.generate(scopeUnit, statement, result);
        result.decIndent();
        result.writeLine("}");
    }

}
