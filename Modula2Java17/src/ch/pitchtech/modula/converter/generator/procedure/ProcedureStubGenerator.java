package ch.pitchtech.modula.converter.generator.procedure;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;

public class ProcedureStubGenerator extends Generator {

    private final ProcedureDefinition procedureDefinition;

    
    public ProcedureStubGenerator(IHasScope scopeUnit, ProcedureDefinition procedureDefinition) {
        super(scopeUnit, procedureDefinition);
        this.procedureDefinition = procedureDefinition;
    }


    @Override
    public void generate(ResultContext result) {
        String name = procedureDefinition.getName();
        
        // Get return type
        String javaReturnType = "void";
        IType pReturnType = procedureDefinition.getReturnType();
        if (pReturnType != null) {
            IType returnType = result.resolveType(pReturnType);
            ResultContext typeContext = result.subContext();
            Types.getGenerator(scopeUnit, returnType).generate(typeContext);
            javaReturnType = typeContext.toString();
        }
        
        // Get arguments
        List<FormalArgument> argumentsToCopy = new ArrayList<>();
        List<FormalArgument> argumentsToWrapAsRef = new ArrayList<>();
        ResultContext argumentsContext = result.subContext();
        boolean isFirst = true;
        for (FormalArgument formalArgument : procedureDefinition.getArguments()) {
            if (!isFirst)
                argumentsContext.write(", ");
            isFirst = false;
            new FormalArgumentGenerator(scopeUnit, formalArgument).generate(argumentsContext, true, argumentsToCopy, argumentsToWrapAsRef);
        }
        String javaArguments = argumentsContext.toString();

        result.writeLine("public " + javaReturnType + " " + name + "(" + javaArguments + ") {");
        result.writeLine("    // todo implement " + name);
        result.writeLine("    throw new UnsupportedOperationException(\"Not implemented: " + name + "\");");
        result.writeLine("}");
        result.writeLn();
    }


}
