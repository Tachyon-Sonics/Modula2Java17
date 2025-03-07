package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.ResultContext;

public interface ITypePreInitializerGenerator {
    
    /**
     * Generate the initial value for a variable of the given type.
     * The result must start with " = ", but must not include the final ";"
     * @param force whether to force a value (true for local variables, false for fields)
     */
    public void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force);
    
    // TODO X default method, with boolean "includesInitialEqual" that delegates and drop the = if requested

}
