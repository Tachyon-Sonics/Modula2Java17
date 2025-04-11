package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.CaseVariantType;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.INamedType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class Types {

    public static Generator getGenerator(IHasScope scopeUnit, IType type) {
        if (type instanceof LiteralType literalType)
            return new LiteralTypeGenerator(scopeUnit, literalType);
        else if (type instanceof OpaqueType opaqueType)
            return new OpaqueTypeGenerator(scopeUnit, opaqueType);
        else if (type instanceof SubrangeType subrangeType)
            return new SubrangeTypeGenerator(scopeUnit, subrangeType);
        else if (type instanceof RangeSetType rangeSetType)
            return new RangeSetTypeGenerator(scopeUnit, rangeSetType);
        else if (type instanceof CaseVariantType caseVariantType)
            return new CaseVariantTypeGenerator(scopeUnit, caseVariantType);
        else if (type instanceof PointerType pointerType)
            return new PointerTypeGenerator(scopeUnit, pointerType);
        else if (type instanceof RecordType recordType)
            return new RecordTypeGenerator(scopeUnit, recordType);
        else if (type instanceof ProcedureType procedureType)
            return new ProcedureTypeGenerator(scopeUnit, procedureType);
        else if (type instanceof ArrayType arrayType)
            return new ArrayTypeGenerator(scopeUnit, arrayType);
        else if (type instanceof OpenArrayType openArrayType)
            return new OpenArrayTypeGenerator(scopeUnit, openArrayType);
        else if (type instanceof EnumerationType enumerationType)
            return new EnumerationTypeGenerator(scopeUnit, enumerationType);
        else if (type instanceof EnumSetType enumSetType)
            return new EnumSetTypeGenerator(scopeUnit, enumSetType);
        throw new CompilerException(scopeUnit, "Unhandled " + type.getClass().getSimpleName());
    }
    
    public static ITypeDefinitionGenerator getTypeDefinitionGenerator(IHasScope scopeUnit, TypeDefinition typeDefinition) {
        IType type = typeDefinition.getType();
        if (type instanceof RecordType recordType)
            return new RecordTypeGenerator(scopeUnit, recordType);
        else if (type instanceof PointerType pointerType)
            return new PointerTypeGenerator(scopeUnit, pointerType);
        else if (type instanceof ProcedureType procedureType)
            return new ProcedureTypeGenerator(scopeUnit, procedureType);
        else if (type instanceof EnumerationType enumerationType)
            return new EnumerationTypeGenerator(scopeUnit, enumerationType);
        else if (type instanceof SubrangeType subrangeType)
            return new SubrangeTypeGenerator(scopeUnit, subrangeType);
        else if (type instanceof RangeSetType rangeSetType)
            return new RangeSetTypeGenerator(scopeUnit, rangeSetType);
        throw new CompilerException(scopeUnit, "Unhandled " + type.getClass().getSimpleName());
    }
    
    public static boolean requiresExplicitDeclaration(IType type) {
        if (type instanceof RecordType || type instanceof ProcedureType || type instanceof EnumerationType) {
            assert type instanceof INamedType;
            return true;
        }
        return false;
    }

}
