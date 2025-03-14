package ch.pitchtech.modula.converter.transform;

import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.DefinitionModule;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.IImplemented;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ILocalVariable;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.type.IType;

/**
 * Mark procedure-local variables as ref ({@link VariableDefinition#setUseRef(boolean)}) when we cannot directly
 * use the corresponding Java type. This occurs when the Java type is a primitive type (int, long, etc) or String,
 * and one of the following:
 * <ul>
 * <li>ADR(...) is used on the variable</li>
 * <li>The variable is passed as a VAR argument of another procedure</li>
 * </ul>
 * Such a marked variable will use {@link Ref} to wrap the actual value. On all normal usage,
 * {@link Ref#get} will be used in the generated Java code, and just the {@link Ref} itself when
 * passed to ADR(...) or to a VAR argument.
 */
public class VariableAsRefMarker {
    // TODO (2) Mark all VAR arguments of procedures used as a procedure type expression. 
    // TODO test a VAR argument that is a POINTER TO INTEGER / RECORD / ARRAY
    
    public final static boolean ENABLE_ARG_AS_REF = true;
    

    public static void apply(ICompilationUnit cu, CompilerOptions compilerOptions) {
        if (cu instanceof IImplemented implemented) {
            for (ProcedureImplementation procedureImplementation : implemented.getProcedures()) {
                processImpl(compilerOptions, procedureImplementation);
            }
        } else if (cu instanceof DefinitionModule definitionModule) {
            if (ENABLE_ARG_AS_REF) {
                if (!definitionModule.isImplemented()) {
                    for (ProcedureDefinition procedureDefinition : definitionModule.getProcedureDefinitions()) {
                        for (FormalArgument argument : procedureDefinition.getArguments()) {
                            if (argument.isVar()) {
                                /*
                                 * If a procedure is in a definition module that has no implementation, it means
                                 * the implementation is done in Java. Hence we have to assume any VAR argument
                                 * can potentially be written and must hence be passed by reference
                                 */
                                processImpl(argument, definitionModule, compilerOptions);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void processImpl(CompilerOptions compilerOptions, ProcedureImplementation procedureImplementation) {
        // Nested procedures
        for (ProcedureImplementation pi : procedureImplementation.getProcedures()) {
            processImpl(compilerOptions, pi);
        }
        
        // Variables
        for (VariableDefinition variableDefinition : procedureImplementation.getVariableDefinitions()) {
            process(variableDefinition, procedureImplementation, compilerOptions);
        }
        
        if (ENABLE_ARG_AS_REF) {
            // Arguments (implementation)
            for (FormalArgument formalArgument : procedureImplementation.getArguments()) {
                if (formalArgument.isVar()) {
                    process(formalArgument, procedureImplementation, compilerOptions);
                }
            }
            
            // Arguments (definition)
            ProcedureDefinition procedureDefinition = procedureImplementation.findDefinition();
            if (procedureDefinition != null) {
                // Also mark arguments of procedure in definition module
                assert procedureDefinition.getArguments().size() == procedureImplementation.getArguments().size();
                for (int i = 0; i < procedureDefinition.getArguments().size(); i++) {
                    FormalArgument implementationArgument = procedureImplementation.getArguments().get(i);
                    if (implementationArgument.isUseRef() && implementationArgument.isVar()) {
                        FormalArgument definitionArgument = procedureDefinition.getArguments().get(i);
                        definitionArgument.setUseRef(true);
                    }
                }
            }
        }
    }

    private static void process(VariableDefinition variableDefinition, IHasScope scopeUnit, CompilerOptions compilerOptions) {
        if (variableDefinition.isAddressed() || variableDefinition.isPassedAsVarAndWritten()) {
            processImpl(variableDefinition, scopeUnit, compilerOptions);
        }
    }

    private static void process(FormalArgument formalArgument, IHasScope scopeUnit, CompilerOptions compilerOptions) {
        // TODO (2) revert once procedures used as procedure type expression are properly marked
        if (formalArgument.isVar() /* && (formalArgument.isAddressed() || formalArgument.isWritten()) */) {
            processImpl(formalArgument, scopeUnit, compilerOptions);
        }
    }

    static void processImpl(ILocalVariable localVariable, IHasScope scopeUnit, CompilerOptions compilerOptions) {
        IType type = TypeResolver.resolveType(scopeUnit.getScope(), localVariable.getType());
        if (TypeHelper.isByValueType(type, scopeUnit.getScope(), compilerOptions)
                // ARRAY OF BYTE argument needs to be an IRef<byte[]> so that Runtime.asByteArray can be used
                || TypeHelper.isOpenArrayOfBytes(type, scopeUnit.getScope())) {
            localVariable.setUseRef(true);
        }
    }

}
