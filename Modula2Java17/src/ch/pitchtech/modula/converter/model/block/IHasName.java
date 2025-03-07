package ch.pitchtech.modula.converter.model.block;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.model.source.SourceElement;

/**
 * Any Modula-2 named element.
 * <p>
 * Used by {@link SourceElement} and {@link Generator} for changing the name when generating the Java code.
 */
public interface IHasName {
    
    public String getName();

}
