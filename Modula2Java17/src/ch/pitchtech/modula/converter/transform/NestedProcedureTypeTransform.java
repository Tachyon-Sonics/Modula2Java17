package ch.pitchtech.modula.converter.transform;

import java.util.ArrayList;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.CompilerOptions;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.IImplemented;
import ch.pitchtech.modula.converter.model.INode;
import ch.pitchtech.modula.converter.model.block.ProcedureImplementation;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

/**
 * Move PROCEDURE type declarations that are inside of a procedure at the module's level
 */
public class NestedProcedureTypeTransform {

    public static void apply(ICompilationUnit cu, CompilerOptions compilerOptions) {
        if (cu instanceof IImplemented module) {
            processImpl(module);
        }
    }
    
    private static void processImpl(INode node) {
        if (node instanceof TypeDefinition typeDefinition) {
            IHasScope parent = typeDefinition.getParent();
            IType type = TypeResolver.resolveProcedureType(typeDefinition.getType(), parent);
            if (type instanceof ProcedureType) {
                if (parent instanceof ProcedureImplementation procedureImplementation) {
                    // 'typeDefinition' is a PROCEDURE TYPE nested into PROCEDURE 'procedureImplementation'
                    String namePrefix = "";
//                    INode beforeNode = parent;
                    IHasScope topLevel = parent;
                    while (topLevel instanceof ProcedureImplementation pi) {
                        namePrefix = pi.getName() + "_" + namePrefix;
//                        beforeNode = pi.getParentInTree();
                        topLevel = pi.getParent();
                    }
                    
                    if (topLevel instanceof ICompilationUnit compilationUnit) {
                        // Remove type from procedure
                        procedureImplementation.removeTypeDefinition(typeDefinition);
                        
                        // Move node to compilation unit
//                        while (beforeNode.getParentNode() != compilationUnit)
//                            beforeNode = beforeNode.getParentNode();
//                        typeDefinition.move(parent, compilationUnit, beforeNode);
                        typeDefinition.remove(parent); // TODO review, does not seem right. Why doesn't compilationUnit have any child?
                        
                        // Add type to compilation unit
                        compilationUnit.getTypeDefinitions().add(typeDefinition);
                        
                        // Rename
                        String newName = namePrefix + typeDefinition.getName();
                        typeDefinition.setNewName(newName);
                    } else {
                        throw new CompilerException(type, "Cannot find compilation unit for {0}. Found {1}", type, topLevel);
                    }
                }
            }
        }
        
        // Recurse
        for (INode child : new ArrayList<>(node.getChildren())) {
            processImpl(child);
        }
    }

}
