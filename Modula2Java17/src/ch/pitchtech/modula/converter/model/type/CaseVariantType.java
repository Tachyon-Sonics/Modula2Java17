package ch.pitchtech.modula.converter.model.type;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class CaseVariantType extends TypeBase implements IType {
    
    private final String variableName;
    private final Map<IExpression, List<VariableDefinition>> variants = new LinkedHashMap<>();
    
    
    public CaseVariantType(SourceLocation sLoc, IHasScope scopeUnit, String variableName) {
        super(sLoc, scopeUnit);
        this.variableName = variableName;
    }
    
    public String getVariableName() {
        return variableName;
    }

    public Map<IExpression, List<VariableDefinition>> getVariants() {
        return variants;
    }

    @Override
    public String toString() {
        return "CaseVariantType [variableName=" + variableName + ", variants=" + variants + "]";
    }

}
