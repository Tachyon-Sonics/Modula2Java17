package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.runtime.Runtime.IRef;

public class TypeHelper {

    public static boolean isCharArrayAsString(IType type, IScope scope, CompilerOptions compilerOptions) {
        if (type instanceof IArrayType arrayType) {
            IType elementType = TypeResolver.resolveType(scope, arrayType.getElementType());
            if (elementType.isBuiltInType(BuiltInType.CHAR) && compilerOptions.isConvertArrayOfCharToString()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isCharArrayAsString(IType type, ResultContext context) {
        return isCharArrayAsString(type, context.getScope(), context.getCompilerOptions());
    }
    
    /**
     * Whether the given type corresponds to a type passed by value in Java.
     * Such types must be wrapped in an appropriate {@link IRef} implementation when
     * passed as a VAR argument, or when addressed with ADR(...).
     * <p>
     * This returns <tt>true</tt> for primitive types, enums, {@link String}, pointer types and opaque types.
     * <p>
     * {@link String} is included because it is not mutable.
     */
    public static boolean isByValueType(IType type, IScope scope, CompilerOptions compilerOptions) {
        return ((type instanceof LiteralType literalType && literalType.isBuiltIn()) // Includes String and ADDRESS
                || type instanceof SubrangeType
                || type instanceof EnumerationType
                || type instanceof PointerType
                || type instanceof OpaqueType
                || isCharArrayAsString(type, scope, compilerOptions));
    }
    
    /**
     * Whether the given type is an open "ARRAY OF BYTE"
     */
    public static boolean isOpenArrayOfBytes(IType type, IScope scope) {
        if (type instanceof OpenArrayType openArrayType) {
            IType elementType = TypeResolver.resolveType(scope, openArrayType.getElementType());
            if (elementType.isBuiltInType(BuiltInType.BYTE))
                return true;
        }
        return false;
    }
    
    /**
     * If the given array expression is a char array variable mapped to a String ref,
     * return the variable definition. Else return null
     */
    public static VariableDefinition getVariableIfStringByRef(IExpression arrayExpr, IHasScope scopeUnit, ResultContext context) {
        if (!context.getCompilerOptions().isConvertArrayOfCharToString())
            return null;
        // TODO check if array of char for safety (although this method is only invoked when it is the case)
        VariableDefinition arrayVariableDefinition = null;
        if (arrayExpr instanceof Identifier identifier) {
            arrayVariableDefinition = scopeUnit.getScope().resolveVariable(identifier.getName());
            if (arrayVariableDefinition != null && arrayVariableDefinition.isUseRef()) {
                return arrayVariableDefinition;
            }
        }
        return null;
    }
    
    /**
     * If the given expression is a variable by ref (generated as an {@link IRef}), return the corresponding
     * {@link VariableDefinition}. Else return <tt>null</tt>.
     */
    public static VariableDefinition getVariableIfByRef(IExpression expression, IHasScope scopeUnit, ResultContext context) {
        if (expression instanceof Identifier identifier) {
            return getVariableIfByRef(identifier, scopeUnit, context);
        }
        return null;
    }
    
    /**
     * If the given identifier is a variable by ref (generated as an {@link IRef}), return the corresponding
     * {@link VariableDefinition}. Else return <tt>null</tt>.
     */
    public static VariableDefinition getVariableIfByRef(Identifier identifier, IHasScope scopeUnit, ResultContext context) {
        IType type = context.resolveType(identifier);
        if (isByValueType(type, context.getScope(), context.getCompilerOptions())) {
            VariableDefinition variableDefinition = scopeUnit.getScope().resolveVariable(identifier.getName());
            if (variableDefinition != null && variableDefinition.isUseRef()) {
                return variableDefinition;
            }
        }
        return null;
    }

}