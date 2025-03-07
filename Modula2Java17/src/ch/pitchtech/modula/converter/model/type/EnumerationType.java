package ch.pitchtech.modula.converter.model.type;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class EnumerationType extends TypeBase implements INamedType {
    
    private String name;
    private final List<String> elements = new ArrayList<>();

    
    public EnumerationType(SourceLocation sLoc, IHasScope scopeUnit, String name) {
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

    public List<String> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "EnumerationType [name=" + name + ", elements=" + elements + "]";
    }

}
