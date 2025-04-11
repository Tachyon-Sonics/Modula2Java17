package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;


public class SubrangeTypeGenerator extends Generator implements ITypeDefinitionGenerator {
    
    private final SubrangeType subrangeType;

    
    public SubrangeTypeGenerator(IHasScope scopeUnit, SubrangeType subrangeType) {
        super(scopeUnit, subrangeType);
        this.subrangeType = subrangeType;
    }

    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        String typeName = typeDefinition.getName();
        if (!subrangeType.getTypeName().equals(typeName))
            throw new CompilerException(typeDefinition, "Inconsistent naming: {0} != {1}", 
                    subrangeType.getTypeName(), typeName);
        String javaQualifiers = scopeUnit.getJavaQualifiers("static", "final");
        result.ensureJavaImport(Runtime.class);
        result.writeIndent();
        result.write(javaQualifiers + "Runtime.Range " + subrangeType.getTypeName() + " = new Runtime.Range(");
        IExpression lowerBound = subrangeType.getLowerBound();
        ResultContext lowerBoundResult = result.subContext();
        Expressions.getGenerator(subrangeType.getDeclaringScope(), scopeUnit.getCompilationUnit(), lowerBound).generate(lowerBoundResult);
        IExpression upperBound = subrangeType.getUpperBound();
        ResultContext upperBoundResult = result.subContext();
        Expressions.getGenerator(subrangeType.getDeclaringScope(), scopeUnit.getCompilationUnit(), upperBound).generate(upperBoundResult);
        result.write(lowerBoundResult.toString() + ", " + upperBoundResult.toString());
        result.write(");");
        result.writeLn();
    }

    @Override
    public void generate(ResultContext result) {
        IType lowerType = subrangeType.getLowerBound().getType(result.getScope());
        IType upperType = subrangeType.getUpperBound().getType(result.getScope());
        if (!lowerType.equals(upperType))
            throw new CompilationException(subrangeType, "lower and upper values of Subrange do not have the same type: " + lowerType.toString() + " vs " + upperType.toString());
        Types.getGenerator(scopeUnit, lowerType).generate(result);
    }

}
