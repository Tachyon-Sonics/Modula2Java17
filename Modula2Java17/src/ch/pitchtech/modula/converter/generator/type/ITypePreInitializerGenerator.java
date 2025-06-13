package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.ResultContext;

public interface ITypePreInitializerGenerator {
    
    /**
     * Generate the initial value for a variable of the given type.
     * The result must not including the initial" = ", and must not include the final ";"
     * @param force whether to force a value (true for local variables, false for fields)
     */
    public void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force);
    
    /**
     * Generate the initial value for a variable of the given type.
     * @param force whether to force a value (true for local variables, false for fields)
     * @param includesInitialEqual whether to start with an initial "<tt> = </tt>".
     */
    public default void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force,
            boolean includesInitialEqual) {
        ResultContext temp = initResult.subContext();
        generateInitializer(beforeResult, temp, force);
        String initStr = temp.toString();
        if (includesInitialEqual) {
            initStr = " = " + initStr;
        }
        initResult.write(initStr);
    }
}
