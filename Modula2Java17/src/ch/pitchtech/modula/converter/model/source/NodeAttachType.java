package ch.pitchtech.modula.converter.model.source;

import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.ForLoop;

public enum NodeAttachType {
    /**
     * Neither read nor written. Typically for statements and declarations
     */
    DEFAULT,
    /**
     * Read access by the correponding node.
     */
    READ_ACCESS,
    /**
     * Write access. Used by {@link Assignement}, {@link ForLoop} and "VAR" arguments in PROCEDURE calls
     */
    WRITE_ACCESS,
    
    /**
     * Use access of parent node. Used by expressions that can be either read or written, like array or field access
     */
    INHERIT_ACCESS;
}
