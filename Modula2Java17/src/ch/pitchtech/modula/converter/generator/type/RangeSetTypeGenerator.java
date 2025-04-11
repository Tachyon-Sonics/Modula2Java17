package ch.pitchtech.modula.converter.generator.type;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;

public class RangeSetTypeGenerator extends Generator implements ITypeDefinitionGenerator, ITypePreInitializerGenerator {
    
    private final RangeSetType rangeSetType;

    
    public RangeSetTypeGenerator(IHasScope scopeUnit, RangeSetType rangeSetType) {
        super(scopeUnit, rangeSetType);
        this.rangeSetType = rangeSetType;
    }

    @Override
    public void generate(ResultContext result) {
        result.ensureJavaImport(Runtime.class);
        result.write("Runtime.RangeSet");
    }

    @Override
    public void generateTypeDefinition(ResultContext result, TypeDefinition typeDefinition) {
        /*
         * "SET OF ..." types are mapped to Runtime.RangeSet, and there is no explicit Java declaration.
         * However, we use the type name (with "_r" suffix) to declare the lower and upper bounds, because they
         * make instanciations of Runtime.RangeSet more clear in the generated Java code.
         */
        String typeName = typeDefinition.getName();
        if (!rangeSetType.getTypeName().equals(typeName + "_r"))
            throw new CompilerException(typeDefinition, "Inconsistant naming: {0} != {1}", 
                    rangeSetType.getTypeName(), typeName + "_r");
        String javaQualifiers = scopeUnit.getJavaQualifiers("static", "final");
        result.ensureJavaImport(Runtime.class);
        result.writeIndent();
        result.write(javaQualifiers + "Runtime.Range " + rangeSetType.getTypeName() + " = new Runtime.Range(");
        IExpression lowerBound = rangeSetType.getLowerBound();
        ResultContext lowerBoundResult = result.subContext();
        Expressions.getGenerator(rangeSetType.getDeclaringScope(), scopeUnit.getCompilationUnit(), lowerBound).generate(lowerBoundResult);
        IExpression upperBound = rangeSetType.getUpperBound();
        ResultContext upperBoundResult = result.subContext();
        Expressions.getGenerator(rangeSetType.getDeclaringScope(), scopeUnit.getCompilationUnit(), upperBound).generate(upperBoundResult);
        result.write(lowerBoundResult.toString() + ", " + upperBoundResult.toString());
        result.write(");");
        result.writeLn();
    }

    @Override
    public void generateInitializer(ResultContext before, ResultContext result, boolean force) {
        result.write(" = ");
        generateInitialValue(result);
    }

    public void generateInitialValue(ResultContext result) {
        if (rangeSetType.getTypeName() != null) {
            result.ensureJavaImport(Runtime.class);
            result.write("new Runtime.RangeSet(");
            qualifyIfNecessary(rangeSetType, result);
            result.write(rangeSetType.getTypeName() + ")");
        } else {
            IExpression lowerBound = rangeSetType.getLowerBound();
            ResultContext lowerBoundResult = result.subContext();
            Expressions.getGenerator(rangeSetType.getDeclaringScope(), scopeUnit.getCompilationUnit(), lowerBound).generate(lowerBoundResult);
            IExpression upperBound = rangeSetType.getUpperBound();
            ResultContext upperBoundResult = result.subContext();
            Expressions.getGenerator(rangeSetType.getDeclaringScope(), scopeUnit.getCompilationUnit(), upperBound).generate(upperBoundResult);
            result.ensureJavaImport(Runtime.class);
            result.write("new Runtime.RangeSet(" + lowerBoundResult.toString() + ", " + upperBoundResult.toString() + ")");
        }
    }

}
