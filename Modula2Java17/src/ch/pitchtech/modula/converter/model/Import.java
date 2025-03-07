package ch.pitchtech.modula.converter.model;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class Import extends SourceElement implements INode {
    
    private final ICompilationUnit compilationUnit;
    private final String fromModule;
    private final boolean qualified;
    private final List<String> items = new ArrayList<>();
    
    
    public Import(SourceLocation sLoc, ICompilationUnit compilationUnit, String fromModule, boolean qualified) {
        super(sLoc);
        this.compilationUnit = compilationUnit;
        this.fromModule = fromModule;
        this.qualified = qualified;
    }
    
    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public String getFromModule() {
        return fromModule;
    }
    
    public List<String> getItems() {
        return items;
    }

    @Override
    public INode getParentNode() {
        return compilationUnit;
    }

    /**
     * @return true: "IMPORT XX;", false: "FROM XX IMPORT ..."
     */
    public boolean isQualified() {
        return qualified;
    }

    @Override
    public String toString() {
        return "Import [fromModule=" + fromModule + ", items=" + items + "]";
    }

}
