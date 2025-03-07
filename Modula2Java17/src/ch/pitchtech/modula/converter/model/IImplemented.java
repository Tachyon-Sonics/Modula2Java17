package ch.pitchtech.modula.converter.model;

import java.util.List;

import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.statement.IStatement;

/**
 * Common interface for {@link ImplementationModule} and {@link Module}.
 */
public interface IImplemented extends ICompilationUnit {
    
    public List<ProcedureImplementation> getProcedures();
    
    public List<IStatement> getBeginStatements();
    
    public List<IStatement> getCloseStatements();

}
