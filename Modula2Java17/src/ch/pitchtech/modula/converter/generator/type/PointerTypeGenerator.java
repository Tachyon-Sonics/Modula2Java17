package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.RecordType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;

public class PointerTypeGenerator extends Generator implements ITypeDefinitionGenerator, ITypePreInitializerGenerator {
    
    private final PointerType pointerType;

    
    public PointerTypeGenerator(IHasScope scopeUnit, PointerType pointerType) {
        super(scopeUnit, pointerType);
        this.pointerType = pointerType;
    }
    
    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        IType targetType = pointerType.getTargetType();
        targetType = result.resolveType(targetType);
        
        if (targetType instanceof RecordType) {
            // Nothing to generate. We will just use the corresponding Object reference
        } else if (targetType instanceof IArrayType) {
            // Nothing to generate. We will just use the corresponding Array reference
        } else if (targetType instanceof LiteralType literalType) {
            if (literalType.isBuiltIn()) {
                result.ensureJavaImport(Runtime.class);
                String name = typeDefinition.getName();
                BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
                String javaBoxedType = builtInType.getBoxedType();
                String javaType = "Runtime.Ref<" + javaBoxedType + ">";
                if (BuiltInType.valueOf(literalType.getName()) == BuiltInType.CHAR 
                        && result.getCompilerOptions().isConvertArrayOfCharToString()) {
                    // POINTER TO ARRAY OF CHAR
                    return; // Do not generate a type definition. It will be replaced by 'String' wherever used
                }
                String javaQualifiers = scopeUnit.getJavaQualifiers("static", null);
                result.writeLine(javaQualifiers + "class " + name + " extends " + javaType + " {");
                result.writeLine("}");
                result.writeLn();
            } else {
                throw new CompilationException(pointerType, "Could not resolve type " + typeDefinition.getName());
            }
        } else { // TODO review other cases
            throw new CompilerException(pointerType, "Unhandled " + targetType.toString());
        }
    }

    @Override
    public void generateInitializer(ResultContext beforeResult, ResultContext initResult, boolean force) {
        generateInitializer(beforeResult, initResult, force, false);
    }

    @Override
    public void generateInitializer(ResultContext before, ResultContext result, boolean force, boolean includesInitialEqual) {
        if (force) {
            if (includesInitialEqual) {
                result.write(" = ");
            }
            result.write("null");
        } else {
            /*
             * Because we do not specify any value, we also never prefix with
             * " = ", regardless of 'includesInitialEqual'
             */
            result.write(" /* POINTER */");
        }
    }

    @Override
    public void generate(ResultContext result) {
        generate(result, false);
    }
    
    public boolean generate(ResultContext result, boolean skipGenerics) {
        IType targetType0 = pointerType.getTargetType();
        IType targetType = result.resolveType(targetType0);
        if (targetType instanceof RecordType recordType) {
            if (targetType0 instanceof LiteralType) {
                new RecordTypeGenerator(scopeUnit, recordType).generate(result);
            } else {
                throw new CompilerException(pointerType, "Unhandled " + String.valueOf(targetType0));
            }
        } else if (targetType instanceof IArrayType arrayType) {
            IType elementType = arrayType.getElementType();
            elementType = result.resolveType(elementType);
            String suffix = "[]";
            while (elementType instanceof IArrayType subArrayType) {
                suffix += "[]";
                elementType = subArrayType.getElementType();
                elementType = result.resolveType(elementType);
            }
            if (elementType instanceof LiteralType literalElementType) {
                String elementJavaType = getJavaTypeFor(result, literalElementType, false);
                if (elementJavaType.equals("String"))
                    suffix = suffix.substring("[]".length()); // POINTER TO ARRAY OF CHAR (with ARRAY OF CHAR as String) -> String TODO this sucks a lot...
                result.write(elementJavaType + suffix);
            } else if (elementType instanceof RangeSetType rangeSetType) {
                ResultContext rsResult = result.subContext();
                new RangeSetTypeGenerator(scopeUnit, rangeSetType).generate(rsResult);
                String elementJavaType = rsResult.toString();
                result.write(elementJavaType + suffix);
            } else if (elementType instanceof RecordType recordType) {
                // POINTER TO ARRAY OF RECORD
                ResultContext recResult = result.subContext();
                new RecordTypeGenerator(scopeUnit, recordType).generate(recResult);
                String elementJavaType = recResult.toString();
                result.write(elementJavaType + suffix);
            } else {
                throw new CompilerException(pointerType, "Unhandled " + String.valueOf(elementType));
            }
        } else if (targetType instanceof LiteralType literalType) {
            result.ensureJavaImport(Runtime.class);
            String targetJavaType = getJavaTypeFor(result, literalType, true);
            if (skipGenerics) {
                result.write("Runtime.IRef");
                return true;
            } else {
                result.write("Runtime.IRef<" + targetJavaType + ">");
            }
        } else if (targetType instanceof PointerType pointerType) {
            // POINTER TO POINTER
            result.ensureJavaImport(Runtime.class);
            if (skipGenerics) {
                result.write("Runtime.IRef");
                return true;
            } else {
                result.write("Runtime.IRef<");
                new PointerTypeGenerator(scopeUnit, pointerType).generate(result, skipGenerics);
                result.write(">");
            }
        } else {
            throw new CompilerException(pointerType, "Unhandled " + String.valueOf(targetType));
        }
        return false;
    }

    private String getJavaTypeFor(ResultContext result, LiteralType literalType, boolean boxed) {
        String targetJavaType;
        if (literalType.isBuiltIn()) {
            BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
            if (boxed)
                targetJavaType = builtInType.getBoxedType();
            else
                targetJavaType = builtInType.getJavaType();
            if (literalType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
                // POINTER TO ARRAY OF CHAR
                // TODO if the pointer is never dereferenced with write access, we could use String instead of Ref<String>
                targetJavaType = "String";
            }
        } else {
            ResultContext literalResult = result.subContext();
            new LiteralTypeGenerator(scopeUnit, literalType).generate(literalResult);
            targetJavaType = literalResult.toString();
        }
        return targetJavaType;
    }

}
