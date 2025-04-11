package ch.pitchtech.modula.converter.compiler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import ch.pitchtech.modula.converter.antlr.m2.m2pim4Parser.CompilationUnitContext;
import ch.pitchtech.modula.converter.model.ICompilationUnit;

public class SourceFile implements Comparable<SourceFile> {
    
    private static final AtomicLong counter = new AtomicLong();
    
    private final Path path;
    private final List<SourceFile> deps = new ArrayList<>(); // Both IMPORTS and .mod -> .def
    private final long id = counter.incrementAndGet();
    
    private CompilationUnitContext cuContext;
    private ICompilationUnit compilationUnit;
    
    
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
