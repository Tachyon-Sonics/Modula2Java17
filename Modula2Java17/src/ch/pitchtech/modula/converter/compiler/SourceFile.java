package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CompilationUnitContext;
import ch.pitchtech.modula.converter.model.ICompilationUnit;

/**
 * Source Modula-2 file.
 * <p>
 * Filled progressively during the compilation steps:
 * <ul>
 * <li>During the initial parsing, imported DEFINITION files are added to {@link #getDeps()}</li>
 * <li>At the end of the initial parsing, {@link #setCuContext(CompilationUnitContext)} is set</li>
 * <li>After abstracting, {@link #setCompilationUnit(ICompilationUnit)} is set</li>
 * <li>{@link #getCompilationUnit()} is used for the code generation</li>
 * </ul>
 * <p>
 * This class implements {@link Comparable} in such a way any source file is before any other
 * source file that depends on it. The resulting order corresponds to the order in which the
 * files must be compiled. The order depends on {@link #getDeps()} and is hence only defined
 * after all files have been parsed. The order is used for all subsequent compilation steps
 * after parsing.
 */
public class SourceFile implements Comparable<SourceFile> {
    
    private static final AtomicLong counter = new AtomicLong();
    
    private final Path path;
    private final List<SourceFile> deps = new ArrayList<>(); // Both IMPORTS and .mod -> .def
    private final long id = counter.incrementAndGet();

    private CompilationUnitContext cuContext;
    private ICompilationUnit compilationUnit;
    private CommonTokenStream tokenStream;
    
    
    public SourceFile(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
    
    public List<SourceFile> getDeps() {
        return deps;
    }
    
    public CompilationUnitContext getCuContext() {
        return cuContext;
    }
    
    public void setCuContext(CompilationUnitContext cuContext) {
        this.cuContext = cuContext;
    }
    
    public ICompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
    
    public void setCompilationUnit(ICompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public CommonTokenStream getTokenStream() {
        return tokenStream;
    }

    public void setTokenStream(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SourceFile other = (SourceFile) obj;
        return Objects.equals(path, other.path);
    }

    @Override
    public int compareTo(SourceFile other) {
        // Put DEFINITIONs first:
        boolean thisDef = SourceFileHelper.isDefinition(this);
        boolean otherDef = SourceFileHelper.isDefinition(other);
        if (thisDef && !otherDef) {
            return -1;
        } else if (!thisDef && otherDef) {
            return 1;
        }
        assert thisDef == otherDef;
        
        // Put dependences first:
        if (this.deps.contains(other)) {
            if (other.deps.contains(this))
                throw new IllegalStateException("Circular dependency between " + this + " and " + other);
            return 1;
        } else if (other.deps.contains(this)) {
            return -1;
        }
        
        // Then creation time
        if (thisDef) {
            return Long.compare(this.id, other.id);
        } else {
            return -Long.compare(this.id, other.id);
        }
    }

    @Override
    public String toString() {
        return "SourceFile [path=" + path + "]";
    }

}
