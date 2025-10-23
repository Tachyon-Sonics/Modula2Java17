package ch.pitchtech.modula.converter.model.scope;

import java.util.BitSet;
import java.util.Collections;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.QualifiedType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class TypeResolver {

    public static IType resolveType(IScope scope, IType type) {
        if (type.getDeclaringScope() != null) {
            return TypeResolver.resolveTypeImpl(type.getDeclaringScope().getScope(), type);
        }
        // Only built-in types have no explicit scope
        if (!(type instanceof LiteralType literalType && literalType.isBuiltIn()))
            throw new CompilationException(type, "Non-literal type has no scope (internal error)");
        // Expand BITSET to RangeSetType
        if (type instanceof LiteralType lit && lit.isBuiltInType(BuiltInType.BITSET)) {
            return expandBitset(lit, scope);
        }
        return type; // Built-in type do not need to be resolved
    }
    
    private static IType resolveTypeImpl(IScope scope, IType type) {
        if (type instanceof LiteralType literalType) {
            // Check if this is BITSET type (by name) that needs expansion
            // We use name comparison here because the literalType might not be marked as built-in yet
            if ("BITSET".equals(literalType.getName())) {
                return expandBitset(literalType, scope);
            }
            if (!(scope instanceof ScopeResolver))
                scope = new ScopeResolver(scope);
            return resolveTypeImpl(scope, literalType.getName());
        } else if (type instanceof QualifiedType qualifiedType) {
            IScope qualifiedScope = qualifiedType.getDeclaringModule().getExportScope();
            return resolveTypeImpl(qualifiedScope, qualifiedType.getName());
        }
        return type;
    }

    private static IType resolveTypeImpl(IScope scope, String typeName) {
        TypeDefinition typeDefinition = scope.resolveType(typeName);
        if (typeDefinition == null)
            throw new CompilationException(scope, "Could not resolve type: " + typeName);
        typeDefinition = getRootType(typeDefinition, scope);
        if (typeDefinition.isOpaque()) {
            ICompilationUnit cu = (ICompilationUnit) typeDefinition.getParentNode();
            return new OpaqueType(typeDefinition.getSourceLocation(), cu, typeDefinition.getName());
        } else {
            return typeDefinition.getType();
        }
    }
    
    /**
     * Resolve the given type until we get a "root" type, that is, a type that is built-in, or a
     * type that is not a literal. The reason is that a literal type that is not built-in is 
     * just an "alias" to the real type (like 'TYPE Alias = RootType')
     */
    private static TypeDefinition getRootType(TypeDefinition typeDefinition, IScope scope) {
        while (typeDefinition.getType() instanceof LiteralType literalType && !literalType.isBuiltIn()) {
            typeDefinition = scope.resolveType(literalType.getName());
        }
        return typeDefinition;
    }

    public static IType resolveProcedureType(IType type, IHasScope scopeUnit) {
        IType result = resolveType(scopeUnit.getScope(), type);
        result = replaceProc(scopeUnit, result);
        return result;
    }
    
    public static IType resolveProcedureType(IExpression expression, IHasScope scopeUnit) {
        IType result = resolveType(scopeUnit.getScope(), expression.getType(scopeUnit.getScope()));
        result = replaceProc(scopeUnit, result);
        return result;
    }
    
    private static IType replaceProc(IHasScope scopeUnit, IType result) {
        if (result instanceof LiteralType literalType && literalType.isBuiltInType(BuiltInType.PROC)) {
            Application application = scopeUnit.getCompilationUnit().getApplication();
            result = new ProcedureType(literalType.getSourceLocation(), application, "PROC", Collections.emptyList(), new BitSet(), null);
        }
        return result;
    }

    /**
     * Expand BITSET to RangeSetType with bounds depending on the data model:
     * - 16-bit data model: SET OF [0..15]
     * - 32-bit data model: SET OF [0..31]
     */
    private static IType expandBitset(LiteralType literalType, IScope scope) {
        int nbBits = CompilerOptions.get().getDataModel().getNbBits();
        int upperBound = nbBits - 1;
        IExpression lowerBoundExpr = new ConstantLiteral(literalType.getSourceLocation(), "0");
        IExpression upperBoundExpr = new ConstantLiteral(literalType.getSourceLocation(), String.valueOf(upperBound));
        // Create a RangeSetType with null typeName so bounds are inlined (not referenced)
        // Use the declaring scope from the original literal type
        IHasScope declaringScope = literalType.getDeclaringScope();
        return new RangeSetType(literalType.getSourceLocation(), declaringScope, null, lowerBoundExpr, upperBoundExpr);
    }

}
