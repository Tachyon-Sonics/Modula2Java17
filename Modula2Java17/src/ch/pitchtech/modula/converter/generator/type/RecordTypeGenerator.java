package ch.pitchtech.modula.converter.generator.type;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.CaseVariantType;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;

// TODO handle inside a record stuff like "point: RECORD x, y: INTEGER END"
// TODO handle "VAR p: RECORD x, y END"
public class RecordTypeGenerator extends Generator implements ITypeDefinitionGenerator, ITypePreInitializerGenerator {
    
    private final RecordType recordType;

    
    public RecordTypeGenerator(IHasScope scopeUnit, RecordType recordType) {
        super(scopeUnit, recordType);
        this.recordType = recordType;
    }

    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        String typeName = typeDefinition.getName();
        
        String javaQualifiers = scopeUnit.getJavaQualifiers("static", null);
        if (result.getCompilerOptions().isUseRecordHelper()) {
            result.ensureJavaImport(Runtime.class);
            result.writeLine(javaQualifiers + "class " + typeName + " extends Runtime.Record<" + typeName + "> { // RECORD");
        } else {
            result.writeLine(javaQualifiers + "class " + typeName + " { // RECORD");
        }
        result.writeLn();
        result.incIndent();
        // Fields
        for (VariableDefinition variableDefinition : recordType.getElements()) {
            if (result.getCompilerOptions().isUseRecordHelper()) {
                IType itemType = result.resolveType(variableDefinition.getType());
                if (itemType instanceof RecordType)
                    result.writeLine("@Runtime.NestedRecord");
                else if (itemType instanceof PointerType)
                    result.writeLine("// POINTER");
            }
            new VariableDefinitionGenerator(scopeUnit, variableDefinition).generate(result);
        }

        // Getter and setters
        result.writeLn();
        result.writeLn();
        for (VariableDefinition variableDefinition : recordType.getExElements()) {
            new VariableDefinitionGenerator(scopeUnit, variableDefinition).generateGetterAndSetter(result);
        }
        
        if (!result.getCompilerOptions().isUseRecordHelper()) {
            result.writeLn();
            // Generate copyFrom() method to support assignement
            result.writeLine("public void copyFrom(" + typeName + " other) {");
            for (VariableDefinition variableDefinition : recordType.getElements()) {
                generateCopyAssignment(result, variableDefinition);
            }
            result.writeLine("}");
            result.writeLn();
            
            // Generate newCopy() method to support pass-by-value
            result.writeLine("public " + typeName + " newCopy() {");
            result.writeLine("    " + typeName + " copy = new " + typeName + "();");
            result.writeLine("    copy.copyFrom(this);");
            result.writeLine("    return copy;");
            result.writeLine("}");
            result.writeLn();
        }
        
        result.decIndent();
        result.writeLine("}");
    }

    private void generateCopyAssignment(ResultContext result, VariableDefinition variableDefinition) {
        String name = variableDefinition.getName();
        IType variableType = result.resolveType(variableDefinition.getType());
        if (variableType instanceof RecordType || variableType instanceof RangeSetType) {
            result.writeLine("    this." + name + ".copyFrom(other." + name + ");");
        } else if (variableType instanceof CaseVariantType caseVariantType) {
            Collection<List<VariableDefinition>> variants = caseVariantType.getVariants().values();
            for (List<VariableDefinition> variantItems : variants) {
                for (VariableDefinition caseVariable : variantItems) {
                    generateCopyAssignment(result, caseVariable);
                }
            }
        } else if (variableType instanceof IArrayType arrayType) {
            if (TypeHelper.isCharArrayAsString(variableType, result)) {
                // String copy
                result.writeLine("    this." + name + " = other." + name + ";");
            } else {
                IType elementType = result.resolveType(arrayType.getElementType());
                boolean deep = (!(elementType instanceof PointerType) 
                        && !(elementType instanceof OpaqueType) 
                        && !elementType.isBuiltInType(BuiltInType.ADDRESS));
                result.ensureJavaImport(Runtime.class);
                result.writeLine("    this." + name + " = Runtime.copyOf(" + deep + ", other." + name + ");");
            }
        } else if (variableType instanceof EnumSetType) {
            result.ensureJavaImport(EnumSet.class);
            result.writeLine("    this." + name + " = EnumSet.copyOf(other." + name + ");");
        } else {
            result.writeLine("    this." + name + " = other." + name + ";");
        }
    }

    @Override
    public void generate(ResultContext result) {
        qualifyIfNecessary(recordType, result);
        String typeName = recordType.getName();
        result.write(typeName);
    }

    @Override
    public void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force) {
        String typeName = recordType.getName();
        initResult.write("new ");
        qualifyIfNecessary(recordType, initResult);
        initResult.write(typeName);
        initResult.write("()");
    }

}
