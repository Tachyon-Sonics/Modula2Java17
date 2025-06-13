package ch.pitchtech.modula.converter.generator.type;

import java.util.EnumSet;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class EnumSetTypeGenerator extends Generator implements ITypePreInitializerGenerator {
    
    private final EnumSetType enumSetType;

    
    public EnumSetTypeGenerator(IHasScope scopeUnit, EnumSetType enumSetType) {
        super(scopeUnit, enumSetType);
        this.enumSetType = enumSetType;
    }

    @Override
    public void generate(ResultContext result) {
        String enumerationTypeName = enumSetType.getEnumerationType().getName();
        TypeDefinition enumerationTypeDef = enumSetType.getDeclaringScope().getScope().resolveType(enumerationTypeName);
        IType enumerationType = result.resolveType(enumerationTypeDef.getType());
        if (enumerationType instanceof SubrangeType subrangeType) {
            // "SET OF Xxx", where "Xxx" is "[lower..upper]"
            // -> RangeSetType
            ICompilationUnit cu = (ICompilationUnit) enumerationTypeDef.getParentNode();
            RangeSetType rangeSetType = new RangeSetType(enumerationTypeDef.getSourceLocation(), cu,
                    subrangeType.getTypeName(), subrangeType.getLowerBound(), subrangeType.getUpperBound());
            new RangeSetTypeGenerator(scopeUnit, rangeSetType).generate(result);
        } else if (enumerationType instanceof EnumerationType enumType) {
            result.ensureJavaImport(EnumSet.class);
            result.write("EnumSet<");
            new EnumerationTypeGenerator(scopeUnit, enumType).generate(result);
            result.write(">");
        }
    }

    @Override
    public void generateInitializer(ResultContext beforeResult, ResultContext result, boolean force) {
        result.write("EnumSet.noneOf(");
        EnumerationType enumerationType = (EnumerationType) result.resolveType(enumSetType.getEnumerationType());
        new EnumerationTypeGenerator(scopeUnit, enumerationType).generate(result);
        result.write(".class)");
    }

}
