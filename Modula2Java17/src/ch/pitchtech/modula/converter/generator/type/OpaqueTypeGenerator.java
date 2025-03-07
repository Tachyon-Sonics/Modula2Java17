package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.OpaqueType;


public class OpaqueTypeGenerator extends Generator {
    
    private final OpaqueType opaqueType;

    
    public OpaqueTypeGenerator(IHasScope scopeUnit, OpaqueType opaqueType) {
        super(scopeUnit, opaqueType);
        this.opaqueType = opaqueType;
    }

    @Override
    public void generate(ResultContext result) {
        String typeName = opaqueType.getName();
        ICompilationUnit compilationUnit = scopeUnit.getCompilationUnit();
        ICompilationUnit declaringUnit = compilationUnit.getCompilationUnitDeclaringType(typeName);
        if (compilationUnit != declaringUnit) {
            DefinitionModule definitionModule = declaringUnit.getDefinitionModule();
            result.write(definitionModule.getName() + ".");
        }
        result.write(typeName);
    }

}
