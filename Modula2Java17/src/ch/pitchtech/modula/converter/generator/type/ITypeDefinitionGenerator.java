package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public interface ITypeDefinitionGenerator {
    
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition);

}
