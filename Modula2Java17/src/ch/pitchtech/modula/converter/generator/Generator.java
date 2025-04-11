package ch.pitchtech.modula.converter.generator;

import java.util.Set;

import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.block.IHasName;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.CurrentFile;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.utils.StringUtils;

public abstract class Generator {
    // TODO GrotteBonus.mod: rename "clock0" to "clock" and debug / handle proper renaming
    
    private final static Set<String> RESERVED_IDENTIFIERS = Set.of(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "default", "do", "double", "else", "enum", "extends", "false", "final", "finally", "float", "for",
            "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "null",
            "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void", "volatile",
            "while", "continue", "record", "var", "yield",
            /* Stuff from java.lang */
            "Boolean", "Byte", "Character", "Double", "Float", "Integer", "Long", "Short", "Runnable", 
            "BitSet", "EnumSet", "Arrays", "Array", "Object", "HaltException",
            /* Runtime */
            "Runtime"
            );

    
    protected final IHasScope scopeUnit;
    private final ICompilationUnit compilationUnit; // If different than scope's; for 'needQualifier' only
    private static SourceElement lastSourceElement;
    
    
    public Generator(IHasScope scopeUnit) {
        this(scopeUnit, scopeUnit.getCompilationUnit(), null);
    }
    
    public Generator(IHasScope scopeUnit, SourceElement forSourceElement) {
        this(scopeUnit, scopeUnit.getCompilationUnit(), forSourceElement);
    }
    
    public Generator(IHasScope scopeUnit, ICompilationUnit compilationUnit) {
        this(scopeUnit, compilationUnit, null);
    }
    
    public Generator(IHasScope scopeUnit, ICompilationUnit compilationUnit, SourceElement forSourceElement) {
        this.scopeUnit = scopeUnit;
        this.compilationUnit = compilationUnit;
        
        if (forSourceElement != null && forSourceElement.getSourceFile() != null) {
            if (forSourceElement.getSourceFile().equals(CurrentFile.getCurrentFile()))
                lastSourceElement = forSourceElement;
        }
    }

    public abstract void generate(ResultContext result);
    
    protected boolean needQualifier(ICompilationUnit declaringUnit) {
        ICompilationUnit compilationUnit = this.compilationUnit;
        if (compilationUnit != declaringUnit) {
            if (compilationUnit instanceof ImplementationModule implementationModule) {
                if (implementationModule.getDefinition() == declaringUnit) {
                    /*
                     * Item is used in IMPLEMENTATION and declared in corresponding DEFINITION.
                     * No need to qualify, because the Java class for implementation extends the Java class for definition.
                     */
                    return false;
                }
            }
            return true; // Different compilation units
        }
        return false; // Same compilation unit
    }
    
    protected final void qualifyIfNecessary(IType type, ResultContext result) {
        if (type.getDeclaringScope() instanceof Application)
            return; // Built-in type
        if (needQualifier(type.getDeclaringUnit()))
            result.write(type.getDeclaringUnit().getDefinitionModule().getName() + ".");
    }

    protected final void qualifyStaticIfNecessary(IHasScope scope, ResultContext result) {
        if (needQualifier(scope.getCompilationUnit()))
            result.write(scope.getCompilationUnit().getDefinitionModule().getName() + ".");
    }

    protected final void qualifyInstanceIfNecessary(IHasScope scope, ResultContext result) {
        if (needQualifier(scope.getCompilationUnit())) {
            String moduleName = scope.getCompilationUnit().getDefinitionModule().getName();
            result.ensureModuleInstance(moduleName);
            result.write(StringUtils.toCamelCase(moduleName) + ".");
        }
    }
    
    public final <E extends SourceElement & IHasName> String name(E element) {
        String name = element.getName();
        if (element.getNewName() != null) {
            // Element was explicitely renamed
            name = element.getNewName();
        }
        if (RESERVED_IDENTIFIERS.contains(name))
            return "_" + name;
        return name;
    }

    public static SourceElement getLastSourceElement() {
        return lastSourceElement;
    }
    
    public static String javaClassName(ICompilationUnit compilationUnit) {
        String result = compilationUnit.getName();
        if (compilationUnit instanceof ImplementationModule) {
            result += "Impl";
        }
        return result;
    }

}
