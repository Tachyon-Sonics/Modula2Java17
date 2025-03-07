package ch.pitchtech.modula.converter.transform;

import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.model.ICompilationUnit;

public class Transforms {
    
    public static void applyTransforms(ICompilationUnit compilationUnit, CompilerOptions compilerOptions) {
        NestedProcedureTypeTransform.apply(compilationUnit, compilerOptions);
        ProcedureAsExpressionMarker.apply(compilationUnit, compilerOptions);
        ReadWriteAnalysis.apply(compilationUnit);
        AddressedAnalysis.apply(compilationUnit);
        ReadWriteNestedProcChainAnalysis.apply(compilationUnit);
        VariableAsWrittenVarArgumentAnalysis.apply(compilationUnit);
        VariableAsRefMarker.apply(compilationUnit, compilerOptions);
        NestedProcedureTransform.apply(compilationUnit, compilerOptions);
    }
    
    /*
     * TODO (2) VAR parameters:
     * o [ok] Need to change records and module vars to getter/setter first (maybe also change DEFINITION to compile into an interface)
     *    - [ok for now] public+(getter+setter if needed) / private+getter+setter / public+getter+setter
     * - never written? use "VAR" for record/array, NOT "VAR" else
     * - [ok] record/array
     *   - VAR: pass ref
     *   - NOT VAR: pass item.copy()
     * - other: promote to IRef<Type>
     *   - callers
     *     - [ok] Already IRef -> pass through
     *     - [ok] Record field -> pass a FieldRef
     *     - [ok] Module variable -> pass a FieldRef
     *     - [ok] Local variable -> promote the variable to a Ref<Type>
     *     - Argument -> either (if not already a IRef):
     *       - promote argument to IRef<Type>, but callers must pass a copy
     *       - rename argument by suffixing a "_", create a local copy into a Ref<Type>
     * - mind PROCEDURE types with VAR arguments
     */

}
