package ch.pitchtech.modula.converter.transform;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.FieldAccess;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.ProcedureExpressionCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

/**
 * Scan for variables passed as a VAR argument that are written at least once.
 * Mark them with {@link VariableDefinition#setPassedAsVarAndWritten(boolean)}.
 * <p>
 * Must be used after {@link ReadWriteAnalysis} to properly detect if the
 * VAR arguments are written or not.
 */
public class VariableAsWrittenVarArgumentAnalysis {

    private final ProcedureImplementation procedureImplementation;
    
    
    public VariableAsWrittenVarArgumentAnalysis(ProcedureImplementation procedureImplementation) {
        this.procedureImplementation = procedureImplementation;
    }

    public static void apply(ICompilationUnit cu) {
        searchIn(cu);
    }
    
    private static void searchIn(INode node) {
        if (node instanceof ProcedureImplementation procedureImplementation) {
            new VariableAsWrittenVarArgumentAnalysis(procedureImplementation).process();
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
            new VariableAsWrittenVarArgumentAnalysis(nestedProc).process();
        }
        for (IStatement statement : procedure.getStatements())
            process(procedure, statement);
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
            if (definition instanceof ProcedureDefinition procedureDefinition) {
                if (!procedureDefinition.isBuiltIn()) {
                    processProcedureCall(curProcedure, methodCall, procedureDefinition);
                    return;
                }
            } else if (definition instanceof VariableDefinition variableDefinition) {
                // TODO consider converting variable procedure call into procedure expression call (with Identifier as expression) so we can get rid of this code
                // Procedure variable call
                IType procType = TypeResolver.resolveProcedureType(variableDefinition.getType(), scopeUnit);
                if (procType instanceof ProcedureType procedureType) {
                    processProcedureVariableCall(curProcedure, methodCall, procedureType);
                    return;
                } else {
                    throw new CompilationException(methodCall, "Identifier is not of procedure type: {0}", variableDefinition);
                }
            } else if (node instanceof ProcedureExpressionCall procedureExpressionCall) {
                // Procedure expression call
                IType procType = TypeResolver.resolveProcedureType(
                        procedureExpressionCall.getProcedureExpr().getType(scopeUnit.getScope()), scopeUnit);
                if (procType instanceof ProcedureType procedureType) {
                    processProcedureVariableCall(curProcedure, methodCall, procedureType);
                    return;
                } else {
                    throw new CompilationException(methodCall, "Expression is not of procedure type: {0}", procedureExpressionCall);
                }
            } else {
                throw new CompilationException(methodCall, "Cannot resolve procedure: {0}", node);
            }
        }

        for (INode child : node.getChildren())
            process(curProcedure, child);
    }

    private void processProcedureCall(ProcedureImplementation curProcedure, IMethodCall procedureCall, ProcedureDefinition procedureDefinition) {
        int nbArgs = procedureDefinition.getArguments().size();
        if (procedureCall.getArguments().size() != nbArgs)
            throw new CompilationException(procedureCall, "Wrong number of arguments");
        for (int i = 0; i < nbArgs; i++) {
            FormalArgument formalArgument = procedureDefinition.getArguments().get(i);
            IExpression actualArgument = procedureCall.getArguments().get(i);
            // TODO add " && formalArgument.isWritten() " once global analysis is enabled
            if (formalArgument.isVar()) {
                mark(actualArgument);
            }
            
            // Argument, if not a VAR, can contain other procedure calls. Recurse into it:
            if (!formalArgument.isVar())
                process(curProcedure, actualArgument);
        }
    }

    private void processProcedureVariableCall(ProcedureImplementation curProcedure, IMethodCall procedureCall, ProcedureType procedureType) {
        int nbArgs = procedureType.getArgumentTypes().size();
        if (procedureCall.getArguments().size() != nbArgs)
            throw new CompilationException(procedureCall, "Wrong number of arguments");
        for (int i = 0; i < nbArgs; i++) {
            IExpression actualArgument = procedureCall.getArguments().get(i);
            if (procedureType.isVarArg(i)) {
                mark(actualArgument); // For a procedure variable we have to assume any VAR arg can be written
            } else {
                // Argument, if not a VAR, can contain other procedure calls. Recurse into it:
                process(curProcedure, actualArgument);
            }
        }
    }
    
    private void mark(INode node) {
        if (node instanceof Identifier identifier) {
            INode parent = identifier.getParentNode();
            if (parent instanceof FieldAccess)
                return; // This is a field access and hence not a local variable
            
            IHasScope scopeUnit = getScopeUnit(identifier);
            IDefinition definition = scopeUnit.getScope().resolve(identifier.getName(), true, false, true, false);
            if (definition instanceof VariableDefinition variableDefinition) {
                if (variableDefinition.getParent() instanceof ProcedureImplementation targetProcedure) {
                    // This is either an argument or a local variable of our procedure
                    FormalArgument arg = targetProcedure.getArgument(variableDefinition.getName());
                    if (arg != null) { // formal argument of 'targetProcedure' is passed as a VAR arg of another procedure
                        arg.setPassedAsVarAndWritten(true);
                    } else { // local variable
                        variableDefinition.setPassedAsVarAndWritten(true);
                    }
                }
            }
        }
    }

}
