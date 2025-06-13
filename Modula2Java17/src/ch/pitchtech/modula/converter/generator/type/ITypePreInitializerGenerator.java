package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.ResultContext;

public interface ITypePreInitializerGenerator {
    
    /**
     * Generate the initial value for a variable of the given type.
     * The result must start with " = ", but must not include the final ";"
     * @param force whether to force a value (true for local variables, false for fields)
     */
    public void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force);
    
    // TODO [X] review, the one with default impl must be the other one
    public default void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force,
            boolean includesInitialEqual) {
        ResultContext temp = initResult.subContext();
        generateInitializer(beforeResult, temp, force);
        String initStr = temp.toString();
        if (!includesInitialEqual && initStr.startsWith(" = ")) {
            initStr = initStr.substring(" = ".length());
        }
        initResult.write(initStr);
    }
}
