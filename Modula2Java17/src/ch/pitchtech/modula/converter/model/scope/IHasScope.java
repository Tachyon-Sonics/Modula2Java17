package ch.pitchtech.modula.converter.model.scope;

import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;

public interface IHasScope extends INode {
    
    public IScope getLocalScope();
    
    public default IScope getScope() {
        return new ScopeResolver(getLocalScope());
    }
    
    /**
     * The Java qualifiers for an item declared in this scope
     */
    public String getJavaQualifierForItems();
    
    /**
     * @param ppQualifier qualifier to add only if a base qualifier such as "public" or "private" exists. It will
     * not be added inside of a procedure for instance.
     * @param alwaysQualifier qualifier to always add
     * @return java qualifiers; empty or ending with " "
     */
    public default String getJavaQualifiers(String ppQualifier, String alwaysQualifier) {
        String result = getJavaQualifierForItems();
        if (!result.isBlank() && ppQualifier != null)
            result += " " + ppQualifier;
        if (!result.isBlank() && alwaysQualifier != null)
            result += " ";
        if (alwaysQualifier != null)
            result += alwaysQualifier;
        if (!result.isBlank())
            result += " ";
        return result;
    }
    
    public default ICompilationUnit getCompilationUnit() {
        if (this instanceof ICompilationUnit cu)
            return cu;
        IScope scope = getLocalScope();
        while (scope != null) {
            if (scope instanceof CompilationUnitScope cus)
                return cus.getCompilationUnit();
            scope = scope.getParentScope();
        }
        return null;
    }

}
