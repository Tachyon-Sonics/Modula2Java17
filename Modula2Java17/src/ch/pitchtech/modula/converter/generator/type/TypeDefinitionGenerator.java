package ch.pitchtech.modula.converter.generator.type;

import java.util.concurrent.atomic.AtomicInteger;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.INamedType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;

public class TypeDefinitionGenerator extends Generator {
    
    private final static AtomicInteger counter = new AtomicInteger();
    private final TypeDefinition typeDefinition;

    
    public TypeDefinitionGenerator(IHasScope scopeUnit, TypeDefinition typeDefinition) {
        super(scopeUnit, typeDefinition);
        this.typeDefinition = typeDefinition;
    }

    @Override
    public void generate(ResultContext result) {
        if (typeDefinition.isOpaque()) {
            // Opaque type. Create an interface TODO X move logic to OpaqueTypeGenerator, make it implement ITypeDefinitionGenerator
            String javaQualifiers = scopeUnit.getJavaQualifiers("static", null);
            result.writeLine(javaQualifiers + "interface " + typeDefinition.getName() + " { // Opaque type");
            result.writeLine("}");
        } else {
            IType type = typeDefinition.getType();
            if (!(type instanceof LiteralType)  // Exclude types that are inlined
                    && !(type instanceof IArrayType) 
                    && !(type instanceof EnumSetType)) {
                boolean nested = typeDefinition.getName().startsWith("<"); // TODO explain (seems to be equal to "<nested>"; use appropriate constant)
                if (nested) {
                    typeDefinition.setName("_Nested" + counter.get());
                    if (type instanceof INamedType namedType) {
                        namedType.setName(typeDefinition.getName());
                    } else if (!(type instanceof PointerType)) {
                        throw new CompilerException(typeDefinition, "Cannot rename nested type {0}, definition {1}", type, typeDefinition);
                    }
                }
                Types.getTypeDefinitionGenerator(scopeUnit, typeDefinition).generateTypeDefinition(result, typeDefinition);
                if (nested && !result.toString().isBlank())
                    counter.getAndIncrement();
            }
        }
    }
    
}
