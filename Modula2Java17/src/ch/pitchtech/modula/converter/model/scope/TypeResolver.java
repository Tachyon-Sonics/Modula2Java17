package ch.pitchtech.modula.converter.model.scope;

import java.util.BitSet;
import java.util.Collections;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.model.Application;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.QualifiedType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class TypeResolver {

    public static IType resolveType(IScope scope, IType type) {
        if (type.getDeclaringScope() != null) {
            return TypeResolver.resolveTypeImpl(type.getDeclaringScope().getScope(), type);
        }
        // Only built-in types have no explicit scope
        if (!(type instanceof LiteralType literalType && literalType.isBuiltIn()))
            throw new CompilationException(type, "Non-literal type has no scope (internal error)");
        return type; // Built-in type do not need to be resolved
    }
    
    private static IType resolveTypeImpl(IScope scope, IType type) {
        if (type instanceof LiteralType literalType) {
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
    
    private static TypeDefinition getRootType(TypeDefinition typeDefinition, IScope scope) {
        if (typeDefinition.getType() instanceof LiteralType literalType) {
            while (!literalType.isBuiltIn()) {
                typeDefinition = scope.resolveType(literalType.getName());
                literalType = (LiteralType) typeDefinition.getType();
            }
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
