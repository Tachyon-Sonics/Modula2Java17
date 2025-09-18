package ch.pitchtech.modula.converter.transform;

import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ILocalData;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.QualifiedAccess;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.ProcedureExpressionCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.utils.Debug;

/**
 * For a {@link ProcedureImplementation}, check what {@link ILocalData} is read or written.
 * <p>
 * This is used to optimize out VAR (or non-VAR) arguments that are never written.
 * Also used to optimally extract nested procedure from their parent (passing only
 * variables that are actually read/written), by {@link ReadWriteNestedProcChainAnalysis}
 * and {@link NestedProcedureTransform}.
 */
public class ReadWriteAnalysis {
    
    private final ProcedureImplementation procedureImplementation;
    private Stack<IExpression> currentVarArguments = new Stack<>();
    
    
    public ReadWriteAnalysis(ProcedureImplementation procedureImplementation) {
        this.procedureImplementation = procedureImplementation;
    }

    public static void apply(ICompilationUnit cu) {
        searchIn(cu);
    }
    
    private static void searchIn(INode node) {
        if (node instanceof ProcedureImplementation procedureImplementation) {
            new ReadWriteAnalysis(procedureImplementation).process();
        } else if (node instanceof ProcedureDefinition procedureDefinition) {
            if (procedureDefinition.getParent() instanceof DefinitionModule definitionModule) {
                if (!definitionModule.isImplemented()) {
                    processDefinitionWithoutImplementation(procedureDefinition);
                }
            }
        } else {
            for (INode child : node.getChildren())
                searchIn(child);
        }
    }
    
    private void process() {
        process(this.procedureImplementation);
    }
    
    private static void processDefinitionWithoutImplementation(ProcedureDefinition procedure) {
        for (FormalArgument argument : procedure.getArguments()) {
            if (argument.isVar()) {
                /*
                 * If a procedure appears in a definition modue that has no implementation, it means
                 * the implementation is done in Java. Hence we assume any VAR argument is potentially
                 * written.
                 */
                argument.setWritten(true);
            }
        }
    }
    
    private void process(ProcedureImplementation procedure) {
        for (ProcedureImplementation nestedProc : procedure.getProcedures()) {
            /*
             * Items read/written by a nested procedure are not considered to be read/written by the containing
             * procedure but by the nested procedure only.
             * Hence analyse with a new instance attached to the nested procedure:
             */
            new ReadWriteAnalysis(nestedProc).process();
        }
        for (IStatement statement : procedure.getStatements())
            process(procedure, statement);
    }
    
    private NodeAttachType getAttachTypeResolve(INode node) {
        NodeAttachType result = getAttachType(node);
        while (result == NodeAttachType.INHERIT_ACCESS) {
            node = node.getParentNode();
            result = getAttachType(node);
        }
        return result;
    }
    
    private IExpression currentVarArgument() {
        if (currentVarArguments.isEmpty())
            return null;
        return currentVarArguments.peek();
    }
    
    private NodeAttachType getAttachType(INode node) {
        if (node == currentVarArgument()) // The node matches a "VAR" actual argument currently being visited
            return NodeAttachType.WRITE_ACCESS;
        return node.getAttachType();
    }
    
    private IHasScope getScopeUnit(INode node) {
        if (node instanceof IHasScope hasScope)
            return hasScope;
        return getScopeUnit(node.getParentNode());
    }
    
    private void process(ProcedureImplementation curProcedure, INode node) {
        if (curProcedure.getName().equals("Auxiliar1"))
            Debug.doNothing();
        // Check for procedure call. The "VAR" keyword makes a write access
        if (node instanceof IMethodCall methodCall) {
            IHasScope scopeUnit = getScopeUnit(methodCall);
            IDefinition definition = methodCall.resolveDefinition(scopeUnit);
            if (definition instanceof ProcedureDefinition procedureDefinition) {
                if (procedureDefinition.isBuiltIn()) {
                    processBuiltInProcedureCall(curProcedure, methodCall, procedureDefinition.getName());
                } else {
                    processProcedureCall(curProcedure, methodCall, procedureDefinition);
                    return;
                }
            } else if (definition instanceof VariableDefinition variableDefinition) {
                // TODO consider converting variable procedure call into procedure expression call (with Indentifier as expression) so we can get rid of this code
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
        
        // Not a procedure call...
        
        // Handle Assignment and ForLoop - use generic detection
        NodeAttachType attachType = getAttachTypeResolve(node);
        if (attachType == NodeAttachType.READ_ACCESS) {
            markRead(node, curProcedure);
        } else if (attachType == NodeAttachType.WRITE_ACCESS) {
            markWrite(node, curProcedure);
        }
        for (INode child : node.getChildren())
            process(curProcedure, child);
    }

    private void processBuiltInProcedureCall(ProcedureImplementation curProcedure, IMethodCall procedureCall, String name) {
        // Handle built-in procedures like "INC" that write their first argument
        BuiltInProcedure builtInProc = BuiltInProcedure.valueOf(name);
        if (builtInProc.isWriteFirstArgument()) {
            if (procedureCall.getArguments().isEmpty())
                throw new CompilationException(procedureCall, "Missing argument");
            IExpression firstArg = procedureCall.getArguments().get(0);
            // No need to recurse: either this is an identifier, or this cannot be an argument (array access or field access)
            markWrite(firstArg, curProcedure);
        }
    }

    private void processProcedureCall(ProcedureImplementation curProcedure, IMethodCall procedureCall, ProcedureDefinition procedureDefinition) {
        // Read/write access depends on whether the argument is "VAR" or not
        int nbArgs = procedureDefinition.getArguments().size();
        if (procedureCall.getArguments().size() != nbArgs)
            throw new CompilationException(procedureCall, "Wrong number of arguments");
        for (int i = 0; i < nbArgs; i++) {
            FormalArgument formalArgument = procedureDefinition.getArguments().get(i);
            IExpression actualArgument = procedureCall.getArguments().get(i);
            if (formalArgument.isVar()) { // TODO global analysis: first check if argument itself is written
                markRead(actualArgument, curProcedure);
                markWrite(actualArgument, curProcedure);
                currentVarArguments.push(actualArgument);
                process(curProcedure, actualArgument);
                currentVarArguments.pop();
            } else {
                markRead(actualArgument, curProcedure);
                process(curProcedure, actualArgument);
            }
        }
    }

    private void processProcedureVariableCall(ProcedureImplementation curProcedure, IMethodCall procedureCall, ProcedureType procedureType) {
        int nbArgs = procedureType.getArgumentTypes().size();
        if (procedureCall.getArguments().size() != nbArgs)
            throw new CompilationException(procedureCall, "Wrong number of arguments");
        for (int i = 0; i < nbArgs; i++) {
            IExpression actualArgument = procedureCall.getArguments().get(i);
            if (procedureType.isVarArg(i)) {
                markRead(actualArgument, curProcedure);
                markWrite(actualArgument, curProcedure);
                currentVarArguments.push(actualArgument);
                process(curProcedure, actualArgument);
                currentVarArguments.pop();
            } else {
                markRead(actualArgument, curProcedure);
                process(curProcedure, actualArgument);
            }
        }
    }
    
    private void markRead(INode node, ProcedureImplementation byProc) {
        mark(node, byProc.getReadData(), ILocalData::setRead);
    }
    
    private void markWrite(INode node, ProcedureImplementation byProc) {
        mark(node, byProc.getWriteData(), ILocalData::setWritten);
    }
    
    private void mark(INode node, Set<ILocalData> addToList, BiConsumer<ILocalData, Boolean> marker) {
        if (node instanceof Identifier identifier) {
            INode parent = identifier.getParentNode();
            if (parent instanceof QualifiedAccess qualifiedAccess && qualifiedAccess.getField() == node)
                return; // This is the field of a qualified access and hence not any markable ILocalData (argument, variable or constant)
            
            IHasScope scopeUnit = getScopeUnit(identifier);
            IDefinition definition = scopeUnit.getScope().resolve(identifier.getName(), true, false, true, false);
            ILocalData localData = null;
            if (definition instanceof VariableDefinition variableDefinition) {
                if (variableDefinition.getParent() instanceof ProcedureImplementation targetProcedure) {
                    // This is either an argument or a local variable of our procedure
                    FormalArgument arg = targetProcedure.getArgument(variableDefinition.getName());
                    if (arg != null) { // This is an argument of target procedure
                        localData = arg;
                    } else {
                        // This is a local variable of target procedure
                        localData = variableDefinition;
                    }
                } else {
                    // This is a variable of a compilation unit
                    localData = variableDefinition;
                }
            } else if (definition instanceof ConstantDefinition constantDefinition) {
                localData = constantDefinition;
            }
            
            if (localData != null) {
                marker.accept(localData, true);
                addToList.add(localData);
            }
        }
    }

}
