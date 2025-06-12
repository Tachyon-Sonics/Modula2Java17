package ch.pitchtech.modula.converter.model.expression;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RecordType;


/**
 * "record.field"
 */
public class FieldAccess extends SourceElement implements IExpression {
    
    private final IExpression expression;
    private final Identifier field;

    
    public FieldAccess(SourceLocation sourceLocation, IExpression expression, Identifier field) {
        super(sourceLocation);
        this.expression = expression;
        this.field = field;
        attach(expression, NodeAttachType.READ_ACCESS);
        attach(field, NodeAttachType.INHERIT_ACCESS);
    }
    
    public IExpression getExpression() {
        return expression;
    }
    
    public Identifier getField() {
        return field;
    }

    @Override
    public boolean isConstant(IScope scope) {
        return false;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        if (expression instanceof Identifier identifier) {
            IDefinition definition = scope.resolve(identifier.getName(), true, false, true, true);
            if (definition == null) {
                // Check for definition module name (qualified field access)
                DefinitionModule definitionModule = scope.resolveModule(identifier.getName());
                if (definitionModule != null) {
                    scope = definitionModule.getExportScope();
                    definition = scope.resolve(field.getName(), true, false, true, true);
                    if (definition == null) {
                        throw new CompilationException(this, "Cannot resolve '{0}' in definition module '{1}'", field.getName(), definitionModule.getName());
                    }
                    if (definition instanceof VariableDefinition variableDefinition) {
                        return variableDefinition.getType();
                    } else if (definition instanceof ConstantDefinition constantDefinition) {
                        return constantDefinition.getValue().getType(scope, forType);
                    } else {
                        throw new CompilerException(this, "Unhandled qualified access of a {0}", definition);
                    }
                }
            }
        }
        
        // Unqualified field access
        IType exprType = TypeResolver.resolveType(scope, expression.getType(scope));
        if (exprType instanceof RecordType recordType) {
            VariableDefinition recordItem = recordType.getExElements().stream().filter(
                    (vd) -> vd.getName().equals(field.getName()))
                    .findFirst().orElse(null);
            if (recordItem == null)
                throw new CompilationException(this, "Record '" + recordType.getName() + "' has no field '" + field.getName() + "'");
            return recordItem.getType();
        } else {
            throw new CompilationException(this, "Expression is not a record: " + expression);
        }
    }

    @Override
    public String toString() {
        return "FieldAccess [expression=" + expression + ", field=" + field + "]";
    }

}
