package ch.pitchtech.modula.converter.model.statement;

/**
 * Implemented by any {@link IStatement} that can contain other {@link IStatement}s
 */
public interface IStatementsContainer {
    
    /**
     * Whether single-line body can be simplified by dropping "{" and "}".
     */
    public final static boolean SIMPLIFY_SINLE_LINE_STATEMENTS = true;

}
