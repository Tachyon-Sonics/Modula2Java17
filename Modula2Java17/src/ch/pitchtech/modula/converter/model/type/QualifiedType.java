package ch.pitchtech.modula.converter.model.type;

import java.util.Objects;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class QualifiedType extends TypeBase implements IType {
    
    private final IHasScope scopeUnit;
    private final String moduleName;
    private final String name;

    
    public QualifiedType(SourceLocation sLoc, IHasScope scopeUnit, String moduleName, String name) {
        super(sLoc, scopeUnit);
        this.scopeUnit = scopeUnit;
        this.moduleName = moduleName;
        this.name = name;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public String getName() {
        return name;
    }

    public DefinitionModule getDeclaringModule() {
        DefinitionModule definitionModule = scopeUnit.getScope().resolveModule(moduleName);
        if (definitionModule == null)
            throw new CompilationException(this, "Cannot resolve module '{0}'", moduleName);
        return definitionModule;
    }

    @Override
    public boolean isBuiltInType(BuiltInType biType2) {
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        QualifiedType other = (QualifiedType) obj;
        return Objects.equals(moduleName, other.moduleName) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "QualifiedType [moduleName=" + moduleName + ", name=" + name + "]";
    }

}
