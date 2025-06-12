package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.expression.FieldAccess;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.utils.StringUtils;


public class FieldAccessGenerator extends Generator {
    
    private final FieldAccess fieldAccess;

    
    public FieldAccessGenerator(IHasScope scopeUnit, FieldAccess fieldAccess) {
        super(scopeUnit, fieldAccess);
        this.fieldAccess = fieldAccess;
    }

    @Override
    public void generate(ResultContext result) {
        // Qualified access
        if (fieldAccess.getExpression() instanceof Identifier identifier) {
            IDefinition definition = result.getScope().resolve(identifier.getName(), true, false, true, true);
            if (definition == null) {
                // Check for definition module name (qualified field access)
                DefinitionModule definitionModule = result.getScope().resolveModule(identifier.getName());
                if (definitionModule != null) {
                    IScope scope = definitionModule.getExportScope();
                    IDefinition fieldDefinition = scope.resolve(fieldAccess.getField().getName(), true, false, true, true);
                    if (fieldDefinition instanceof ConstantDefinition) {
                        // Static access
                        result.write(definitionModule.getName());
                    } else {
                        // Instance access
                        result.write(StringUtils.toCamelCase(definitionModule.getName()));
                    }
                    result.write(".");
                    result.write(fieldAccess.getField().getName());
                    return;
                }
            }
        }

        // Unqualified access
        Expressions.getGenerator(scopeUnit, fieldAccess.getExpression()).generate(result);
        result.write(".");
        result.write(fieldAccess.getField().getName());
    }

}
