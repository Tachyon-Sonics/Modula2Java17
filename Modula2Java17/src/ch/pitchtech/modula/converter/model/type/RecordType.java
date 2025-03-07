package ch.pitchtech.modula.converter.model.type;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class RecordType extends TypeBase implements INamedType, IByReferenceValueType {
    
    private String name;
    private final List<VariableDefinition> elements = new ArrayList<>();
    
    
    public RecordType(SourceLocation sLoc, IHasScope scopeUnit, String name) {
        super(sLoc, scopeUnit);
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<VariableDefinition> getElements() {
        return elements;
    }
    
    /**
     * Get all elements, extending case variants
     */
    public List<VariableDefinition> getExElements() {
        List<VariableDefinition> result = new ArrayList<>();
        for (VariableDefinition vd : getElements()) {
            addExElements(vd, result);
        }
        return result;
    }
    
    private void addExElements(VariableDefinition vd, List<VariableDefinition> result) {
        if (vd.getType() instanceof CaseVariantType caseVariant) {
            for (List<VariableDefinition> vList : caseVariant.getVariants().values()) {
                for (VariableDefinition vvd : vList)
                    addExElements(vvd, result);
            }
        } else {
            result.add(vd);
        }
    }

    @Override
    public String toString() {
        return "RecordType [name=" + name + ", elements=" + elements + "]";
    }

}
