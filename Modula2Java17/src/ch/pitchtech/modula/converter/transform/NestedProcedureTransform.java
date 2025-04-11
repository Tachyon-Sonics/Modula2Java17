package ch.pitchtech.modula.converter.transform;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.ImplementationModule;
import ch.pitchtech.modula.converter.model.Module;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.FormalArgument;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.type.IType;

public class NestedProcedureTransform {
    
    public final static boolean USE_READ_WRITE_ANALYSIS = true;
    
    
    public static void apply(ICompilationUnit compilationUnit, CompilerOptions compilerOptions) {
        if (compilationUnit instanceof ImplementationModule implementationModule) {
            for (ProcedureImplementation procedureImplementation : new ArrayList<>(implementationModule.getProcedures()))
                apply(procedureImplementation, compilerOptions);
        } else if (compilationUnit instanceof Module module) {
            for (ProcedureImplementation procedureImplementation : new ArrayList<>(module.getProcedures()))
                apply(procedureImplementation, compilerOptions);
        }
    }
    
    private static void apply(ProcedureImplementation procedureImplementation, CompilerOptions compilerOptions) {
        for (ProcedureImplementation nestedProc : procedureImplementation.getProcedures()) {
            // Collect nested procedure's local variables and arguments, they shadow any variables or arguments of containing procedure
            Set<String> localVariableNames = new HashSet<>();
            for (ConstantDefinition constantDefinition : nestedProc.getConstantDefinitions())
                localVariableNames.add(constantDefinition.getName());
            for (VariableDefinition variableDefinition : nestedProc.getVariableDefinitions())
                localVariableNames.add(variableDefinition.getName());
            for (FormalArgument argument : nestedProc.getArguments())
                localVariableNames.add(argument.getName());
            nestedProc.setVariableNames(localVariableNames);
        }
            
        // Recurse
        for (ProcedureImplementation nestedProc : new ArrayList<>(procedureImplementation.getProcedures())) {
            apply(nestedProc, compilerOptions);
        }
        
        for (ProcedureImplementation nestedProc : procedureImplementation.getProcedures()) {
            Set<String> localVariableNames = nestedProc.getVariableNames();
            Set<String> accessedNames = nestedProc.getAccessedNames();
            Set<String> writtenOrAddressedNames = nestedProc.getWrittenOrAddressedNames();
            
            // Add nesting procedure's arguments into nested
            for (FormalArgument arg : procedureImplementation.getArguments()) {
                if (!localVariableNames.contains(arg.getName()) && accessed(accessedNames, arg.getName())) {
                    // TODO (3) unvar if not written by nested proc
                    FormalArgument argCopy = new FormalArgument(arg.getSourceLocation(), arg.isVar(), arg.getName(), arg.getType());
                    if (writtenOrAddressedNames.contains(arg.getName())) {
                        VariableAsRefMarker.processImpl(arg, procedureImplementation, compilerOptions);
                    }
                    argCopy.copyAccessDataFrom(arg);
                    argCopy.setSurrogateFor(arg);
                    nestedProc.addArgument(argCopy);
                }
            }
            // Add nesting procedure's constants into nested
            for (ConstantDefinition constant : procedureImplementation.getConstantDefinitions()) {
                if (!localVariableNames.contains(constant.getName()) && accessed(accessedNames, constant.getName())) {
                    IScope scope = procedureImplementation.getScope();
                    IType constantType = TypeResolver.resolveType(scope, constant.getValue().getType(scope));
                    FormalArgument arg = new FormalArgument(constant.getSourceLocation(), false, constant.getName(), constantType);
                    arg.copyAccessDataFrom(constant);
                    nestedProc.addArgument(arg);
                }
            }
            // Add nesting procedure's variable into nested
            for (VariableDefinition variable : procedureImplementation.getVariableDefinitions()) {
                if (!localVariableNames.contains(variable.getName()) && accessed(accessedNames, variable.getName())) {
                    boolean writtenOrAddressed = writtenOrAddressedNames.contains(variable.getName());
                    FormalArgument arg = new FormalArgument(variable.getSourceLocation(), writtenOrAddressed, variable.getName(), variable.getType());
                    if (writtenOrAddressed) {
                        // TODO does not look good. We need to put this transform before other analyses and fix the resulting bugs
                        VariableAsRefMarker.processImpl(variable, procedureImplementation, compilerOptions);
                    }
                    arg.copyAccessDataFrom(variable);
                    if (!writtenOrAddressed) {
                        arg.setUseRef(false);
                        arg.setAddressed(false);
                        arg.setWritten(false);
                        arg.setPassedAsVarAndWritten(false);
                    }
//                    arg.setSurrogateFor(variable);
                    nestedProc.addArgument(arg);
                }
            }
            // Setup prefix
            nestedProc.insertPrefix(procedureImplementation.getName() + "_");
            // Add nested proc into parent 
            // TODO nestedProc.getParentNode() is still the nesting procedure (and is needed by ProcedureCallGenerator; this sucks)
            INode parentNode = procedureImplementation.getParentNode();
            if (parentNode instanceof ProcedureImplementation parentProcedure)
                addBefore(parentProcedure::addProcedureBefore, procedureImplementation, nestedProc);
            else if (parentNode instanceof ImplementationModule implementationModule)
                addBefore(implementationModule::addProcedureBefore, procedureImplementation, nestedProc);
            else if (parentNode instanceof Module module)
                addBefore(module::addProcedureBefore, procedureImplementation, nestedProc);
            else
                throw new CompilerException(nestedProc, "Cannot find its parent");
        }
        // We cannot do that: the procedure must still be found in the nesting's scope
//        procedureImplementation.getProcedures().clear();
    }
    
    private static boolean accessed(Set<String> accessedNames, String name) {
        if (USE_READ_WRITE_ANALYSIS)
            return accessedNames.contains(name);
        return true;
    }
    
    private static void addBefore(BiConsumer<ProcedureImplementation, ProcedureImplementation> beforeAdder,
            ProcedureImplementation before, ProcedureImplementation procedure) {
        beforeAdder.accept(before, procedure);
    }

}
