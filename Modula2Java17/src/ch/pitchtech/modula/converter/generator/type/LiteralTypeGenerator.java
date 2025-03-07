package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.LiteralType;

public class LiteralTypeGenerator extends Generator {
    
    private final LiteralType literalType;

    
    public LiteralTypeGenerator(IHasScope scopeUnit, LiteralType literalType) {
        super(scopeUnit, literalType);
        this.literalType = literalType;
    }

    @Override
    public void generate(ResultContext result) {
        String typeName = literalType.getName();
        if (literalType.isBuiltIn()) {
            BuiltInType bType = BuiltInType.valueOf(typeName);
            if (bType == BuiltInType.STRING && !result.getCompilerOptions().isConvertArrayOfCharToString()) {
                result.write("char[]");
            } else {
                String javaType = bType.getJavaType();
                result.write(javaType);
            }
        } else {
            ICompilationUnit compilationUnit = scopeUnit.getCompilationUnit();
            ICompilationUnit declaringUnit = compilationUnit.getCompilationUnitDeclaringType(typeName);
            if (declaringUnit == null)
                throw new CompilationException(literalType, "Could not resolve type {0}", typeName);
            if (compilationUnit != declaringUnit) {
                DefinitionModule definitionModule = declaringUnit.getDefinitionModule();
                result.write(definitionModule.getName() + ".");
            }
            result.write(typeName);
        }
    }

}
