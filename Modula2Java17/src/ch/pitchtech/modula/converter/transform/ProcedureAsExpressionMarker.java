package ch.pitchtech.modula.converter.transform;

import java.util.ArrayList;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.IImplemented;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

/**
 * Mark procedures that are used as expression (for exemple as the right-side of
 * an assignment to a procedure-type variable).
 * <p>
 * For such procedure, we will have to generate a reference using the Java's
 * "class::myProcedure" syntax.
 */
public class ProcedureAsExpressionMarker {

    public static void apply(ICompilationUnit cu, CompilerOptions compilerOptions) {
        if (compilerOptions.isInlineProcedureAsExpression())
            return;
        if (cu instanceof IImplemented module) {
            processImpl(module);
        }
    }
    
    private static void processImpl(INode node) {
        // Procedure expression can only appear in assignment (right-side) or method call (as an argument)
        if (node instanceof Assignement assignment) {
            IExpression target = assignment.getTarget();
            IExpression value = assignment.getValue();
            if (value instanceof Identifier identifier) {
                String name = identifier.getName();
                IHasScope scopeUnit = identifier.getScopeUnit();
                IDefinition definition = scopeUnit.getScope().resolve(name, true, false, true, true);
                if (definition instanceof ProcedureDefinition procedureExprDef) {
                    IType targetType = target.getType(scopeUnit.getScope());
                    IType resolvedType = TypeResolver.resolveProcedureType(targetType, scopeUnit);
                    if (resolvedType instanceof ProcedureType procedureType) {
                        procedureExprDef.getUsedAsExprTypes().add(procedureType);
                    } else {
                        throw new CompilerException(target, "Not a procedure type: {0}", resolvedType);
                    }
                }
            }
        } else if (node instanceof IMethodCall methodCall) {
            for (int argIndex = 0; argIndex < methodCall.getArguments().size(); argIndex++) {
                IExpression arg = methodCall.getArguments().get(argIndex);
                if (arg instanceof Identifier identifier) {
                    String name = identifier.getName();
                    IHasScope scopeUnit = identifier.getScopeUnit();
                    IDefinition definition = scopeUnit.getScope().resolve(name, true, false, true, true);
                    if (definition instanceof ProcedureDefinition procedureExprDef) {
                        // We have an argument that is a procedure
                        IDefinition methodDefinition = methodCall.resolveDefinition(scopeUnit);
                        if (methodDefinition instanceof ProcedureDefinition procedureDefinition) {
                            // TODO (4) handle "SomeProc(ADR(someOtherProc));". Maybe base on arg type rather than arg being an Identifier
                            if (!(procedureDefinition.isBuiltIn() && procedureDefinition.getName().equals("ADR"))) {
                                IType type = procedureDefinition.getArguments().get(argIndex).getType();
                                IType resolvedType = TypeResolver.resolveProcedureType(type, procedureDefinition.getParent());
                                if (resolvedType instanceof ProcedureType procedureType) {
                                    procedureExprDef.getUsedAsExprTypes().add(procedureType);
                                } else {
                                    throw new CompilerException(arg, "Not a procedure type: {0}", resolvedType);
                                }
                            }
                        } else if (methodDefinition instanceof VariableDefinition) {
                            // TODO Procedure variable invoked with a procedure as argument
                        }
                    }
                }
            }
        }
        
        // Recurse
        for (INode child : new ArrayList<>(node.getChildren())) {
            processImpl(child);
        }
    }
}
