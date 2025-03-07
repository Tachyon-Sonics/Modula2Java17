package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.procedure.FormalArgumentGenerator;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.converter.transform.VariableAsRefMarker;

public class ProcedureTypeGenerator extends Generator implements ITypeDefinitionGenerator {
    
    private final ProcedureType procedureType;

    
    public ProcedureTypeGenerator(IHasScope scopeUnit, ProcedureType procedureType) {
        super(scopeUnit, procedureType);
        this.procedureType = procedureType;
    }

    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        // Get return type
        String javaReturnType = "void";
        if (procedureType.getReturnType() != null) {
            IType returnType = result.resolveType(procedureType.getReturnType());
            ResultContext typeContext = result.subContext();
            Types.getGenerator(scopeUnit, returnType).generate(typeContext);
            javaReturnType = typeContext.toString();
        }
        
        // Get argument types
        ResultContext argumentsContext = result.subContext();
        int index = 0;
        for (IType argumentType : procedureType.getArgumentTypes()) {
            if (index > 0)
                argumentsContext.write(", ");
            IType argType = result.resolveType(argumentType);
            FormalArgument formalArgument = new FormalArgument(
                    procedureType.getSourceLocation(), 
                    procedureType.isVarArg(index), 
                    "arg" + (index + 1), 
                    argType);
            if (VariableAsRefMarker.ENABLE_ARG_AS_REF)
                formalArgument.setUseRef(formalArgument.isVar());
            new FormalArgumentGenerator(scopeUnit, formalArgument).generate(argumentsContext);
            index++;
        }
        String javaArguments = argumentsContext.toString();
        
        result.writeLine("@FunctionalInterface");
        String javaQualifier = scopeUnit.getJavaQualifiers("static", null);
        result.writeLine(javaQualifier + "interface " + name(typeDefinition) + " { // PROCEDURE Type");
        result.writeLine("    public " + javaReturnType + " invoke(" + javaArguments + ");");
        result.writeLine("}");
    }

    @Override
    public void generate(ResultContext result) {
        if (procedureType.isProc()) {
            result.ensureJavaImport(Runnable.class);
            result.write(Runnable.class.getSimpleName());
        } else {
            qualifyIfNecessary(procedureType, result);
            result.write(name(procedureType));
        }
    }

}
