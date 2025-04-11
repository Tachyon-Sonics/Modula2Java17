package ch.pitchtech.modula.converter.transform;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ILocalData;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.statement.IStatement;

/**
 * If a nested PROCEDURE X calls another nested PROCEDURE Y, adds all read and written locals of Y to those of X
 */
public class ReadWriteNestedProcChainAnalysis {

    private final boolean nested;
    private final ProcedureImplementation procedureImplementation;
    
    
    public ReadWriteNestedProcChainAnalysis(boolean nested, ProcedureImplementation procedureImplementation) {
        this.nested = nested;
        this.procedureImplementation = procedureImplementation;
    }

    public static void apply(ICompilationUnit cu) {
        searchIn(cu);
    }
    
    private static void searchIn(INode node) {
        if (node instanceof ProcedureImplementation procedureImplementation) {
            new ReadWriteNestedProcChainAnalysis(false, procedureImplementation).process();
        } else {
            for (INode child : node.getChildren())
                searchIn(child);
        }
    }
    
    private void process() {
        process(this.procedureImplementation);
    }
    
    private void process(ProcedureImplementation procedure) {
        for (ProcedureImplementation nestedProc : procedure.getProcedures()) {
            new ReadWriteNestedProcChainAnalysis(true, nestedProc).process();
        }
        if (nested) {
            for (IStatement statement : procedure.getStatements())
                process(procedure, statement);
        }
    }
    
    private IHasScope getScopeUnit(INode node) {
        if (node instanceof IHasScope hasScope)
            return hasScope;
        return getScopeUnit(node.getParentNode());
    }
    
    private void process(ProcedureImplementation curProcedure, INode node) {
        // Check for procedure call.
        if (node instanceof IMethodCall methodCall) {
            IHasScope scopeUnit = getScopeUnit(methodCall);
            IDefinition definition = methodCall.resolveDefinition(scopeUnit);
            if (definition instanceof ProcedureDefinition calledDefinition) {
                if (calledDefinition.getParent() instanceof ProcedureImplementation parentProcedure) {
                    /* 
                     * This is a call from nested PROCEDURE 'curProcedure' to another nested PROCEDURE 'procedureDefinition'.
                     * Transfer read and written stuff
                     */
                    ProcedureImplementation calledProcedure = parentProcedure.getProcedure(calledDefinition.getName());
                    if (calledProcedure == null)
                        throw new CompilationException(methodCall, "Cannot resolve PROCEDURE {0} in {1}", calledDefinition.getName(), parentProcedure);
                    
                    for (ILocalData localData : calledProcedure.getWriteData()) {
                        // Skip calledProcedure's own arguments and variables
                        if (localData.getParentNode() != calledDefinition && localData.getParentNode() != calledProcedure) {
                            curProcedure.getWriteData().add(localData);
                        }
                    }
                    for (ILocalData localData : calledProcedure.getReadData()) {
                        // Skip calledProcedure's own arguments and variables
                        if (localData.getParentNode() != calledDefinition && localData.getParentNode() != calledProcedure) {
                            curProcedure.getReadData().add(localData);
                        }
                    }
                }
            }
        }
        
        for (INode child : node.getChildren())
            process(curProcedure, child);
    }

}
