package ch.pitchtech.modula.converter.generator.procedure;

import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IByReferenceValueType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.runtime.Runtime;

public class FormalArgumentGenerator extends Generator {
    
    private final FormalArgument formalArgument;

    
    public FormalArgumentGenerator(IHasScope scopeUnit, FormalArgument formalArgument) {
        super(scopeUnit);
        this.formalArgument = formalArgument;
    }
    
    /**
     * Generates a formal argument for a definition. Use {@link #generate(ResultContext, boolean, List)}
     * for the implementation
     */
    @Override
    public void generate(ResultContext result) {
        generate(result, false, null, null);
    }

    public void generate(ResultContext result, boolean implementation, List<FormalArgument> argumentsToCopy, List<FormalArgument> argumentsToWrapInRef) {
        boolean isVar = formalArgument.isVar();
        boolean isWritten = formalArgument.isWritten();
        boolean isAddressed = formalArgument.isAddressed();
        boolean isPassedAsVarAndWritten = formalArgument.isPassedAsVarAndWritten();

        String preInfo;
        if (implementation) {
            preInfo = (isVar ? (isWritten ? "/* VAR */ " : "/* var */ ") : "");
            if (isPassedAsVarAndWritten) {
                if (preInfo.contains(" */"))
                    preInfo = preInfo.replace(" */", "+WRT */");
                else
                    preInfo += "/* WRT */ ";
            }
        } else {
            preInfo = (isVar ? "/* VAR */ " : "");
        }
        result.write(preInfo);
        
        IType argType = result.resolveType(formalArgument.getType());
        boolean isByRef = (argType instanceof IByReferenceValueType);
        if (TypeHelper.isCharArrayAsString(argType, result))
            isByRef = false; // This is "String", which is by-ref but immutable (hence can be handled like by-value)
        String argumentName = name(formalArgument);
        
        boolean needsCopy = implementation
                && isByRef 
                && !isVar 
                && (isWritten || !result.getCompilerOptions().isOptimizeUnwrittenByRefArguments());
        if (needsCopy) {
            argumentsToCopy.add(formalArgument);
            argumentName = "_" + argumentName;
        }
        
        boolean needsWrapToRef = implementation
                && !isByRef
                && !isVar
                && (isPassedAsVarAndWritten || isAddressed); // TODO test the 'isAddressed' case
        if (needsWrapToRef) {
            argumentsToWrapInRef.add(formalArgument);
            argumentName = "_" + argumentName;
        }
        
        if (formalArgument.isUseRef()) {
            // TODO X merge with common code in VariableDefinitionGenerator
            result.ensureJavaImport(Runtime.class);
            result.write("Runtime.IRef<");
            if (argType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
                String javaBoxedType = builtInType.getBoxedType();
                result.write(javaBoxedType);
            } else {
                Types.getGenerator(scopeUnit, argType).generate(result);
            }
            result.write(">");
        } else {
            Types.getGenerator(scopeUnit, argType).generate(result);
        }
        result.write(" ");
        result.write(argumentName);
    }

}
