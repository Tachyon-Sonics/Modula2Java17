package ch.pitchtech.modula.converter.model.scope;

import java.util.BitSet;
import java.util.Collections;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.compiler.DataModelType;
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

    /**
     * Resolve the given type.
     * <ul>
     * <li>If the type is a user-defined type (<tt>TYPE MyType = ...</tt>), return the underlying type (resolving
     * the full chain of aliases if necessary)</li>
     * <li>If the type is a known Modula-2 structure (record, array, etc), return it as is</li>
     * <li>If the type is a built-in type (INTEGER, etc), return it as is, except <tt>BITSET</tt> that is treated
     * as an alias to {@link RangeSetType} (to simplify compilation)</li>
     * </ul>
     */
    public static IType resolveType(IScope scope, IType type) {
        IType result;
        if (type.getDeclaringScope() != null) {
            result = TypeResolver.resolveTypeImpl(type.getDeclaringScope().getScope(), type);
        } else {
            // Only built-in types have no explicit scope
            if (!(type instanceof LiteralType literalType && literalType.isBuiltIn()))
                throw new CompilationException(type, "Non-literal type has no scope (internal error)");
            
            result = type;
        }
        
        result = replaceAnyBuiltInType(type.getDeclaringScope(), result);

        return result;
    }

    /**
     * If {@code type} is a built-in type, check if that type must further be replaced.
     * <p>
     * Actually this method only replaces the built-in type {@link BuiltInType#BITSET} by
     * a {@link RangeSetType} with bounds 0 and {@link DataModelType#getNbBits()} - 1.
     * <p>
     * In all other cases, {@code type} is returned unmodified.
     */
    private static IType replaceAnyBuiltInType(IHasScope scopeUnit, IType type) {
        if (type instanceof LiteralType literalType && literalType.isBuiltInType(BuiltInType.BITSET)) {
            int nbBits = CompilerOptions.get().getDataModel().getNbBits();
            type = new RangeSetType(null, scopeUnit, "BITSET", true, 
                    new ConstantLiteral(null, "0"), 
                    new ConstantLiteral(null, String.valueOf(nbBits - 1)));
        }
        return type;
    }
    
    /**
     * Resolve the given type, that is known not to be a built-in type.
     */
    private static IType resolveTypeImpl(IScope scope, IType type) {
        if (type instanceof LiteralType literalType) {
            assert !literalType.isBuiltIn(); // Should have been cought by resolveType()
            // Literal user-defined type: use the scope in argument
            if (!(scope instanceof ScopeResolver))
                scope = new ScopeResolver(scope);
            return resolveTypeImpl(scope, literalType.getName());
        } else if (type instanceof QualifiedType qualifiedType) {
            // Qualified user-defined type: use the qualifier (a module) as scope
            IScope qualifiedScope = qualifiedType.getDeclaringModule().getExportScope();
            return resolveTypeImpl(qualifiedScope, qualifiedType.getName());
        }
        // Anything else: an existing Modula-2 structure (record, array, etc). Return as is.
        return type;
    }

    /**
     * Resolve the given type that is known to be a user-defined type
     * @param scope the scope in which the type name is used
     * @param typeName the name of the user-defined type
     */
    private static IType resolveTypeImpl(IScope scope, String typeName) {
        // Find the type definition in the given scope
        TypeDefinition typeDefinition = scope.resolveType(typeName);
        if (typeDefinition == null)
            throw new CompilationException(scope, "Could not resolve type: " + typeName);
        
        // If the type definition is an alias to another type definition, get the root one.
        typeDefinition = getRootType(typeDefinition, scope);
        
        // Extract the type from the type definition
        if (typeDefinition.isOpaque()) {
            // For an opaque type definition, the underlying type was not specified and is hence null.
            ICompilationUnit cu = (ICompilationUnit) typeDefinition.getParentNode();
            return new OpaqueType(typeDefinition.getSourceLocation(), cu, typeDefinition.getName());
        } else {
            return typeDefinition.getType();
        }
    }
    
    /**
     * Resolve the given type definition until we get a "root" type definitoon, that is, one whose
     * type that is built-in, or a type that is not a literal.
     * <p>
     * The reason is that a literal type that is not built-in is 
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
    
}
