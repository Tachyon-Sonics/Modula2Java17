package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;

public class OpenArrayTypeGenerator extends Generator {
    
    private final OpenArrayType openArrayType;

    
    public OpenArrayTypeGenerator(IHasScope scopeUnit, OpenArrayType openArrayType) {
        super(scopeUnit, openArrayType);
        this.openArrayType = openArrayType;
    }

    @Override
    public void generate(ResultContext result) {
        LiteralType elementType0 = openArrayType.getElementType();
        IType elementType = result.resolveType(elementType0);
        if (elementType instanceof LiteralType literalType) {
            if (literalType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
                result.write("String");
            } else {
                new LiteralTypeGenerator(scopeUnit, literalType).generate(result);
                result.write("[]");
            }
        } else if (elementType0 instanceof LiteralType literalType) {
            new LiteralTypeGenerator(scopeUnit, literalType).generate(result);
            result.write("[]");
        } else {
            throw new CompilerException(openArrayType, "Unhandled element type " + elementType);
        }
    }

}
