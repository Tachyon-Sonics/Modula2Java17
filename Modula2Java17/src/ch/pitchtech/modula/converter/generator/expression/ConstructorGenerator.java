package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.expression.Constructor;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RecordType;


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
            if (targetType instanceof RecordType recordType) {
                result.write("new ");
                qualifyIfNecessary(recordType, result);
                result.write(recordType.getName());
                result.write("()");
            } else {
                // TODO (2) handle simple types, arrays, etc
                throw new CompilationException(constructor, "Unimplementer POINTER TO {0}", targetType);
            }
        } else {
            throw new CompilerException(constructor, "Pointer type expected for constructor");
        }
    }

}
