package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.IHasName;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;

public class Identifier extends SourceElement implements IExpression, IHasName {
    
    private final IHasScope scopeUnit;
    private final String name;

    
    public Identifier(SourceLocation sLoc, IHasScope scopeUnit, String name) {
        super(sLoc);
        this.scopeUnit = scopeUnit;
        this.name = name;
    }
    
    public IHasScope getScopeUnit() {
        return scopeUnit;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConstant(IScope scope) {
        if (name.equals("TRUE") || name.equals("FALSE"))
            return true;
        if (name.equals("NIL"))
            return true;
        scope = scope.full();
        IDefinition definition = scope.resolve(name, true, false, true, true);
        if (definition instanceof ConstantDefinition)
            return true;
        else if (definition instanceof VariableDefinition variableDefinition && variableDefinition.getType() instanceof EnumerationType)
            return true; // This is an enum member
        return false;
    }

    @Override
    public Object evaluateConstant() {
        if (name.equals("TRUE"))
            return true;
        else if (name.equals("FALSE"))
            return false;
        return null;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        if (name.equals("TRUE") || name.equals("FALSE"))
            return new LiteralType(BuiltInType.BOOLEAN);
        if (name.equals("NIL"))
            return new LiteralType(BuiltInType.ADDRESS);
        scope = scope.full();
        IDefinition definition = scope.resolve(name, true, false, true, true);
        if (definition == null)
            throw new CompilationException(this, "Cannot resolve type of: " + name);
        if (definition instanceof ConstantDefinition constantDefinition)
            return constantDefinition.getValue().getType(constantDefinition.getParent().getScope());
        if (definition instanceof VariableDefinition variableDefinition)
            return variableDefinition.getType();
        if (definition instanceof ProcedureDefinition procedureDefinition)
            return new ProcedureType(procedureDefinition);
        throw new CompilationException(this, "Cannot resolve type of: " + name);
    }

    @Override
    public String toString() {
        return "Identifier [name=" + name + "]";
    }

}
