package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.IExpression;

/**
 * Implemented by expressions that (may) generate a different code for write access (compared to read access)
 */
public interface IAssignGenerator {
    
    /**
     * @param value the value to assign this element to
     * @return whether a write access was generated (without ending ";"). Else, nothing has been generated and read-access
     * fallback will be used
     */
    public boolean generateAssignement(ResultContext result, ResultContext assignedValue);
    
    public boolean generateAssignement(ResultContext result, IExpression value);

}
