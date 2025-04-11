package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;


public class EnumerationTypeGenerator extends Generator implements ITypeDefinitionGenerator {
    
    private final EnumerationType enumerationType;

    
    public EnumerationTypeGenerator(IHasScope scopeUnit, EnumerationType enumerationType) {
        super(scopeUnit, enumerationType);
        this.enumerationType = enumerationType;
    }

    @Override
    public void generate(ResultContext result) {
        ICompilationUnit declaringUnit = enumerationType.getDeclaringUnit();
        if (needQualifier(declaringUnit)) {
            // Import enum type
            String targetPackage;
            if (declaringUnit.getDefinitionModule().isImplemented())
                targetPackage = result.getCompilerOptions().getTargetPackageMain();
            else
                targetPackage = result.getCompilerOptions().getTargetPackageLib();
            result.ensureJavaImport(targetPackage + "." 
                    + declaringUnit.getDefinitionModule().getName() + "."
                    + enumerationType.getName());
        }
        result.write(enumerationType.getName());
    }

    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        String javaQualifiers = scopeUnit.getJavaQualifiers("static", null);
        result.writeLine(javaQualifiers + "enum " + enumerationType.getName() + " {");
        for (int i = 0; i < enumerationType.getElements().size(); i++) {
            String itemName = enumerationType.getElements().get(i);
            boolean isLast = (i == enumerationType.getElements().size() - 1);
            result.writeLine("    " + itemName + (isLast ? ";" : ","));
        }
        result.writeLine("}");
    }

}
