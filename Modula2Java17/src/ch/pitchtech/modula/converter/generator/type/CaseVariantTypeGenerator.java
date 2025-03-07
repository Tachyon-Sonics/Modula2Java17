package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.CaseVariantType;

public class CaseVariantTypeGenerator extends Generator {
    
    private final CaseVariantType caseVariantType;

    
    public CaseVariantTypeGenerator(IHasScope scopeUnit, CaseVariantType caseVariantType) {
        super(scopeUnit, caseVariantType);
        this.caseVariantType = caseVariantType;
    }

    @Override
    public void generate(ResultContext result) {
        String varName = caseVariantType.getVariableName();
        result.writeLine("// CASE \"" + varName + "\" {");
        for (IExpression expr : caseVariantType.getVariants().keySet()) {
            for (VariableDefinition variableDefinition : caseVariantType.getVariants().get(expr)) {
                new VariableDefinitionGenerator(scopeUnit, variableDefinition).generateCaseVariantItem(expr, result);
            }
        }
        result.writeLine("// }");
        
    }

}
