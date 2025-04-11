package ch.pitchtech.modula.converter.generator.field;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.type.EnumSetTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.LiteralTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.RangeSetTypeGenerator;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;

public class ConstantDefinitionGenerator extends Generator {

    private final ConstantDefinition constantDefinition;
    private final IHasScope scopeUnit;
    

    public ConstantDefinitionGenerator(ConstantDefinition constantDefinition, IHasScope scopeUnit) {
        super(scopeUnit, constantDefinition);
        this.constantDefinition = constantDefinition;
        this.scopeUnit = scopeUnit;
    }

    @Override
    public void generate(ResultContext result) {
        result.writeIndent();
        String javaQualifiers = scopeUnit.getJavaQualifiers("static", "final");
        result.write(javaQualifiers);
        IType type = result.resolveType(constantDefinition.getValue());
        if (type instanceof LiteralType literalType) {
            new LiteralTypeGenerator(scopeUnit, literalType).generate(result);
        } else if (type instanceof EnumSetType enumSetType) {
            new EnumSetTypeGenerator(scopeUnit, enumSetType).generate(result);
        } else if (type instanceof RangeSetType rangeSetType) {
            new RangeSetTypeGenerator(scopeUnit, rangeSetType).generate(result);
        } else {
            throw new CompilationException(constantDefinition, "Constant " + constantDefinition.getName() + " is not of supported type: " + type);
        }
        result.write(" ");
        result.write(constantDefinition.getName());
        result.write(" = ");
        Expressions.getGenerator(scopeUnit, constantDefinition.getValue()).generate(result);
        result.write(";");
        result.writeLn();
    }
    
}
