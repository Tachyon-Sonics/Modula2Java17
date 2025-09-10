package ch.pitchtech.modula.converter.transform;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ILocalData;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.Dereference;
import ch.pitchtech.modula.converter.model.expression.QualifiedAccess;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RecordType;

/**
 * Scan the code and mark all {@link ILocalData} that are addressed using "ADR(...)".
 * <p>
 * {@link ILocalData} are marked using {@link ILocalData#setAddressed(boolean)}
 * <p>
 * When procedure-local variables of primitive type are addressed, they must be wrapped
 * into an <tt>IRef</tt> when converted to Java
 */
public class AddressedAnalysis { // TODO merge common code with ReadWriteAnalysis and VariableAsVarArgumentAnalysis

    private final ProcedureImplementation procedureImplementation;
    
    
    public AddressedAnalysis(ProcedureImplementation procedureImplementation) {
        this.procedureImplementation = procedureImplementation;
    }

    public static void apply(ICompilationUnit cu) {
        searchIn(cu);
    }
    
    private static void searchIn(INode node) {
        if (node instanceof ProcedureImplementation procedureImplementation) {
            new AddressedAnalysis(procedureImplementation).process();
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
            /*
             * Items addressed by a nested procedure are not considered to be adressed by the containing
             * procedure but by the nested procedure only.
             * Hence analyse with a new instance attached to the nested procedure:
             */
            new AddressedAnalysis(nestedProc).process();
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
        if (node instanceof FunctionCall functionCall) {
            if (functionCall.isBuiltIn(BuiltInProcedure.ADR)) {
                if (functionCall.getArguments().size() != 1)
                    throw new CompilationException(functionCall, "Exactly 1 argument expected for ADR. Found: {0}", functionCall.getArguments());
                IExpression expression = functionCall.getArguments().get(0);
                while (expression instanceof ParenthesedExpression parExpr)
                    expression = parExpr.getTarget();
                if (expression instanceof ArrayAccess) {
                    // Nothing to do. ArrayElementRef handles it...
                } else if (expression instanceof Dereference) {
                    // Nothing to do: 'ADR(ptr^)' is equivalent to just 'ptr' 
                } else if (expression instanceof QualifiedAccess qualifiedAccess) {
                    IExpression faExpression = qualifiedAccess.getExpression();
                    IScope faScope = getScopeUnit(faExpression).getScope();
                    IType faType = TypeResolver.resolveType(faScope, faExpression.getType(faScope));
                    
                    Identifier field = qualifiedAccess.getField();
                    if (faType instanceof RecordType recordType) {
                        VariableDefinition recordItem = recordType.getExElements().stream().filter(
                                (vd) -> vd.getName().equals(field.getName()))
                                .findFirst().orElse(null);
                        if (recordItem == null)
                            throw new CompilationException(node, "Record '" + recordType.getName() + "' has no field '" + field.getName() + "'");
                        recordItem.setAddressed(true);
                    } else {
                        throw new CompilerException(node, "Record is not of record type: {0}", faType);
                    }
                } else if (expression instanceof Identifier identifier) {
                    IHasScope scopeUnit = getScopeUnit(identifier);
                    IDefinition definition = scopeUnit.getScope().resolve(identifier.getName(), true, false, true, true);
                    if (definition instanceof ILocalData localData0) {
                        ILocalData localData = localData0;
                        if (localData instanceof VariableDefinition variableDefinition) {
                            if (variableDefinition.getSurrogateFor() != null)
                                localData = variableDefinition.getSurrogateFor();
                        }
                        // VariableDefinition, ConstantDefinition or FormalArgument
                        localData.setAddressed(true);
                        curProcedure.getAddressedData().add(localData);
                    }
                } else if (expression instanceof StringLiteral) {
                    // Nothing to do
                } else {
                    // - InExpression, InfixExpression, MinusExpression, SetExpression: not allowed
                    throw new CompilationException(node, "Cannot use ADR on expression {0}", expression);
                }
            }
        }

        for (INode child : node.getChildren())
            process(curProcedure, child);
    }

}
