package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.field.VariableDefinitionGenerator;
import ch.pitchtech.modula.converter.generator.type.ITypePreInitializerGenerator;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.expression.Constructor;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.runtime.Runtime;


public class ConstructorGenerator extends Generator {
    
    private final Constructor constructor;

    
    public ConstructorGenerator(IHasScope scopeUnit, Constructor constructor) {
        super(scopeUnit);
        this.constructor = constructor;
    }


    @Override
    public void generate(ResultContext result) {
        IType type = constructor.getType(result.getScope());
        if (type instanceof PointerType pointerType) {
            IType targetType = pointerType.getTargetType();
            targetType = result.resolveType(targetType);
            Generator targetTypeGenerator = Types.getGenerator(scopeUnit, targetType);
            
            if (targetType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                result.ensureJavaImport(Runtime.class);
                result.write("new Runtime.Ref<");
                
                // Not necessary if we rely on Java's diamond operator:
//                BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
//                result.write(builtInType.getBoxedType());
                
                result.write(">(");
                
                /*
                 * Interesting dilema: do we default to no value (resulting to NullPointerException is read before
                 * written), or to default value?
                 * Here we choose to default to the default value...
                 */
                VariableDefinitionGenerator.generateInitialValue(constructor, scopeUnit, literalType, result, false);
                
                result.write(")");
            } else if (targetType instanceof PointerType) {
                // Although PointerType implements ITypePreInitializerGenerator, its default value is null.
                // Here we need to create a non-null pointer instead using Runtime.Ref
                result.ensureJavaImport(Runtime.class);
                result.write("new Runtime.Ref<>()");
            } else if (targetTypeGenerator instanceof ITypePreInitializerGenerator initializerGenerator) {
                /*
                 * NEW is similar to the logic of giving the default value to a variable of the pointed type.
                 * Reuse the same logic:
                 */
                ResultContext initResult = result.subContext();
                ResultContext beforeResult = result.subContext();
                initializerGenerator.generateInitializer(beforeResult, initResult, true, false);
//                result.write(beforeResult); // TODO review, what do we do with that if non-empty?
                result.write(initResult);
            } else {
                throw new CompilerException(constructor, "Unimplemented POINTER TO {0}", targetType);
            }
        } else {
            throw new CompilerException(constructor, "Pointer type expected for constructor");
        }
    }

}
