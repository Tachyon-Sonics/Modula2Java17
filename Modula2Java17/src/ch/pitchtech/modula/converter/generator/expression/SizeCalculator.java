package ch.pitchtech.modula.converter.generator.expression;

import java.util.function.Function;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;

public class SizeCalculator {
    
    public static int getJavaSizeOf(Object element, ResultContext context, IType type) {
        return getSizeOf(element, context, type, BuiltInType::getJavaSize);
    }
    
    public static int getModulaSizeOf(Object element, ResultContext context, IType type) {
        return getSizeOf(element, context, type, BuiltInType::getModulaSize);
    }
    
    private static int getSizeOf(Object element, ResultContext context, IType type, Function<BuiltInType, Integer> which) {
        if (type instanceof LiteralType literalType) {
            if (literalType.isBuiltIn()) {
                BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                return which.apply(biType);
            } else {
                throw new CompilerException(element, "Unhandled LiteralType that is not a built-in type: {0}", literalType);
            }
        } else if (type instanceof PointerType || type instanceof ProcedureType || type instanceof OpaqueType) {
            return which.apply(BuiltInType.ADDRESS);
        } else if (type instanceof EnumerationType) {
            return 1;
        } else if (type instanceof RangeSetType || type instanceof EnumSetType) {
            return which.apply(BuiltInType.BITSET);
        } else if (type instanceof ArrayType) {
            IType elementType = type;
            int multiplier = 1;
            while (elementType instanceof ArrayType arrayType) {
                Object upper0 = arrayType.getUpperBound().evaluateConstant();
                Object lower0 = arrayType.getLowerBound().evaluateConstant();
                if (upper0 instanceof Number upper && lower0 instanceof Number lower) {
                    int size = upper.intValue() - lower.intValue() + 1;
                    multiplier *= size;
                } else {
                    throw new CompilerException(element, "Failed to evaluate constant bounds {0} .. {1} as numbers", arrayType.getLowerBound(), arrayType.getUpperBound());
                }
                elementType = context.resolveType(arrayType.getElementType());
            }
            return getSizeOf(element, context, elementType, which) * multiplier;
        } else if (type instanceof RecordType recordType) {
            int result = 0;
            for (VariableDefinition item : recordType.getElements()) {
                IType itemType = context.resolveType(item.getType());
                result += getSizeOf(element, context, itemType, which);
            }
            return result;
        } else {
            throw new CompilerException(element, "Unhandled type for SIZE/TSIZE: {0}", type);
        }
    }

}
