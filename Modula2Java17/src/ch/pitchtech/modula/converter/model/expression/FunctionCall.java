package ch.pitchtech.modula.converter.model.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.statement.IMethodCall;
import ch.pitchtech.modula.converter.model.type.AdrPointerType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

public class FunctionCall extends SourceElement implements IExpression, IMethodCall {
    
    private final IHasScope scopeUnit; // Defining module if qualified, caller's scope else
    private final String moduleName;
    private final String functionName;
    private final List<IExpression> arguments = new ArrayList<>();
            
    
    /**
     * @param moduleName optional module name (if qualified access). <tt>null</tt> if this is an unqualified access
     */
    public FunctionCall(SourceLocation sLoc, IHasScope scopeUnit, String moduleName, String functionName) {
        super(sLoc);
        this.scopeUnit = scopeUnit;
        this.moduleName = moduleName;
        this.functionName = functionName;
    }
    
    public String getModuleName() { // TODO test qualified access
        return moduleName;
    }

    public String getFunctionName() {
        return functionName;
    }

    /**
     * Get the definition module if this call is qualified, or <tt>null</tt> else
     */
    public IHasScope getQualifiedScope() {
        if (moduleName != null)
            return this.scopeUnit;
        return null;
    }
    
    @Override
    public List<IExpression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    public void addArgument(IExpression argument) {
        this.arguments.add(argument);
        attach(argument, NodeAttachType.READ_ACCESS);
    }
    
    public void addArguments(List<IExpression> arguments) {
        this.arguments.addAll(arguments);
        attach(arguments, NodeAttachType.READ_ACCESS);
    }
    
    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }
    
    public boolean isBuiltIn(BuiltInProcedure builtInFunc) {
        ProcedureDefinition functionDefinition = scopeUnit.getScope().resolveProcedure(functionName);
        if (functionDefinition != null && functionDefinition.isBuiltIn()) {
            BuiltInProcedure builtInProc = BuiltInProcedure.valueOf(functionDefinition.getName());
            return builtInProc == builtInFunc;
        }
        return false;
    }
    
    public IScope getScopeUnit(IScope callerScope) {
        if (moduleName != null) {
            return this.scopeUnit.getScope(); // Use qualified scope
        } else {
            return callerScope; // Unqualified access: use caller's scope
        }
    }

    @Override
    public Object evaluateConstant() {
        ProcedureDefinition functionDefinition = scopeUnit.getScope().resolveProcedure(functionName);
        if (functionDefinition != null && functionDefinition.isBuiltIn()) {
            BuiltInProcedure builtInProc = BuiltInProcedure.valueOf(functionDefinition.getName());
            if (builtInProc == BuiltInProcedure.MIN || builtInProc == BuiltInProcedure.MAX) {
                if (arguments.size() == 1) {
                    if (arguments.get(0) instanceof Identifier typeIdentifier) {
                        LiteralType argType = new LiteralType(getSourceLocation(), scopeUnit, typeIdentifier.getName(), false);
                        IType resultType = TypeResolver.resolveType(scopeUnit.getScope(), argType);
                        if (resultType.isBuiltInType(BuiltInType.BOOLEAN)) {
                            // MIN(BOOLEAN) or MAX(BOOLEAN)
                            if (builtInProc == BuiltInProcedure.MIN)
                                return false;
                            else
                                return true;
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        ProcedureDefinition functionDefinition = getScopeUnit(scope).full().resolveProcedure(functionName);
        if (functionDefinition == null) {
            // Look for a variable of procedure type
            VariableDefinition variableDefinition = scope.full().resolveVariable(functionName);
            if (variableDefinition != null) {
                IType varType = TypeResolver.resolveType(scope.full(), variableDefinition.getType());
                if (varType instanceof ProcedureType procedureType)
                    return procedureType.getReturnType();
            }
            throw new CompilationException(this, "Cannot resolve procedure: {0}", functionName);
        }
        if (functionDefinition.isBuiltIn()) {
            BuiltInProcedure builtInProc = BuiltInProcedure.valueOf(functionDefinition.getName());
            if (forType instanceof LiteralType literalType) {
                return builtInProc.getReturnTypeFor(literalType);
            } else if (forType instanceof PointerType pointerType && builtInProc == BuiltInProcedure.ADR) {
                IExpression argument = getArguments().get(0);
                IType argType = TypeResolver.resolveType(scope, argument.getType(scope));
                if (argType instanceof PointerType)
                    return pointerType;
                else
                    return new AdrPointerType(getSourceLocation(), scopeUnit, argument, argType);
            } else {
                if (builtInProc == BuiltInProcedure.MIN || builtInProc == BuiltInProcedure.MAX) {
                    // The argument is the type itself
                    if (arguments.size() != 1)
                        throw new CompilationException(this, "Expected one argument. Found {0}", arguments.size());
                    if (arguments.get(0) instanceof Identifier typeIdentifier) {
                        LiteralType argType = new LiteralType(getSourceLocation(), scopeUnit, typeIdentifier.getName(), false);
                        IType resultType = TypeResolver.resolveType(scope, argType);
                        if (!(resultType instanceof EnumerationType))
                            return resultType;
                    } else {
                        throw new CompilationException(this, "Expected one type identifier argument. Found {0}", arguments);
                    }
                } else if (builtInProc == BuiltInProcedure.VAL) {
                    // The first argument is the type itself
                    if (arguments.size() != 2)
                        throw new CompilationException(this, "Expected 2 arguments. Found {0}", arguments.size());
                    if (arguments.get(0) instanceof Identifier typeIdentifier) {
                        LiteralType argType = new LiteralType(getSourceLocation(), scopeUnit, typeIdentifier.getName(), false);
                        IType resultType = TypeResolver.resolveType(scope, argType);
                        if (!(resultType instanceof EnumerationType))
                            return resultType;
                    } else {
                        throw new CompilationException(this, "Expected type identifier as 1st argument. Found {0}", arguments.get(0));
                    }
                }
                return new LiteralType(builtInProc.getReturnType());
            }
        } else {
            return functionDefinition.getReturnType();
        }
    }

    @Override
    public IDefinition resolveDefinition(IHasScope scopeUnit) {
        if (moduleName != null) {
            // Qualified name: use scope of module qualifier
            return this.scopeUnit.getScope().resolve(functionName, false, false, true, true);
        } else {
            // Unqualified access: use supplied caller scope
            return scopeUnit.getScope().resolve(functionName, false, false, true, true);
        }
    }

    @Override
    public ProcedureType resolveType(IHasScope scopeUnit) {
        IDefinition definition = resolveDefinition(scopeUnit);
        if (definition instanceof ProcedureDefinition procedureDefinition)
            return new ProcedureType(procedureDefinition);
        else if (definition instanceof VariableDefinition variableDefinition) {
            IType type = variableDefinition.getType();
            if (type instanceof ProcedureType procedureType)
                return procedureType;
            else
                throw new CompilationException(this, "Not a procedure: {0}", functionName);
        } else {
            throw new CompilationException(this, "Cannot resolve: {0}", functionName);
        }
    }

    @Override
    public String toString() {
        return "FunctionCall [moduleName=" + moduleName + ", functionName=" + functionName + ", arguments=" + arguments + "]";
    }

}
