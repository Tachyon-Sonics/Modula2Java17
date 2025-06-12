package ch.pitchtech.modula.converter.model.statement;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

/**
 * Common base class for {@link FunctionCall} and {@link ProcedureCall}.
 */
public abstract class StaticCall extends SourceElement implements IMethodCall {

    protected final IHasScope scopeUnit; // Defining module if qualified, caller's scope else
    protected final String moduleName; // Module name if qualified, null else

    
    public StaticCall(SourceLocation sourceLocation, IHasScope scopeUnit, String moduleName) {
        super(sourceLocation);
        this.scopeUnit = scopeUnit;
        this.moduleName = moduleName;
    }
    
    protected abstract String getName();

    @Override
    public IDefinition resolveDefinition(IHasScope callerScope) {
        if (moduleName != null) {
            // Qualified access: use scope of module qualifier
            if (this.scopeUnit instanceof DefinitionModule definitionModule) {
                return definitionModule.getExportScope().resolve(getName(), false, false, true, true);
            } else {
                // This should not happen... qualified access are always from a definition module
                return this.scopeUnit.getScope().resolve(getName(), false, false, true, true);
            }
        } else {
            // Unqualified access: use supplied caller scope
            return callerScope.getScope().resolve(getName(), false, false, true, true);
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
                throw new CompilationException(this, "Not a procedure/function: {0}", getName());
        } else {
            throw new CompilationException(this, "Cannot resolve: {0}", getName());
        }
    }

}
