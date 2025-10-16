package ch.pitchtech.modula.converter.generator.expression;

import java.util.EnumSet;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.type.EnumerationTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.RangeSetTypeGenerator;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression.SetRange;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.converter.model.type.SubrangeType;
import ch.pitchtech.modula.runtime.Runtime;


public class SetExpressionGenerator extends Generator {
    
    private final SetExpression setExpression;

    
    public SetExpressionGenerator(IHasScope scopeUnit, SetExpression setExpression) {
        super(scopeUnit, setExpression);
        this.setExpression = setExpression;
    }

    @Override
    public void generate(ResultContext result) {
        IType type = result.resolveType(setExpression.getSetType());
        
        if (type instanceof EnumSetType enumSetType) {
            IType itemType = result.resolveType(enumSetType.getEnumerationType());
            if (itemType instanceof SubrangeType subrangeType) {
                // "SET OF XX", where XX is a named subrange type, is detected as EnumSetType, but should actually be RangeSetType
                // TODO handle using a transformation?
                RangeSetType rangeSetType = new RangeSetType(
                        enumSetType.getSourceLocation(), 
                        enumSetType.getDeclaringScope(), 
                        subrangeType.getTypeName(),
                        false,
                        subrangeType.getLowerBound(),
                        subrangeType.getUpperBound());
                type = rangeSetType;
            }
        }
        
        if (type instanceof EnumSetType enumSetType) {
            result.ensureJavaImport(EnumSet.class);
            ResultContext midContext = result.subContext();
            ResultContext frontContext = result.subContext();
            ResultContext backContext = result.subContext();
            
            // Individual elements
            if (setExpression.getSetValues().isEmpty()) {
                midContext.write("EnumSet.noneOf(");
                EnumerationType enumerationType = (EnumerationType) result.resolveType(enumSetType.getEnumerationType());
                new EnumerationTypeGenerator(scopeUnit, enumerationType).generate(midContext);
                midContext.write(".class)");
            } else {
                midContext.write("EnumSet.of(");
                boolean isFirst = true;
                for (IExpression element : setExpression.getSetValues()) {
                    if (!isFirst)
                        midContext.write(", ");
                    isFirst = false;
                    Expressions.getGenerator(scopeUnit, element).generate(midContext);
                }
                midContext.write(")");
            }
            
            // Ranges
            for (SetRange range : setExpression.getSetRanges()) {
                // Merge previous
                ResultContext prev = result.subContext();
                prev.write(frontContext);
                prev.write(midContext);
                prev.write(backContext);
                midContext = prev;
                frontContext = result.subContext();
                backContext = result.subContext();
                
                // Add range
                result.ensureJavaImport(Runtime.class);
                frontContext.write("Runtime.withRange(");
                backContext.write(", ");
                Expressions.getGenerator(scopeUnit, range.lower()).generate(backContext);
                backContext.write(", ");
                Expressions.getGenerator(scopeUnit, range.upper()).generate(backContext);
                backContext.write(")");
            }
            
            result.write(frontContext);
            result.write(midContext);
            result.write(backContext);
        } else if (type instanceof RangeSetType rangeSetType) {
            new RangeSetTypeGenerator(scopeUnit, rangeSetType).generateInitialValue(result);
            if (!setExpression.getSetValues().isEmpty()) {
                result.write(".with(");
                boolean isFirst = true;
                for (IExpression element : setExpression.getSetValues()) {
                    if (!isFirst)
                        result.write(", ");
                    isFirst = false;
                    Expressions.getGenerator(scopeUnit, element).generate(result);
                }
                result.write(")");
            }
            for (SetRange range : setExpression.getSetRanges()) {
                result.write(".withRange(");
                Expressions.getGenerator(scopeUnit, range.lower()).generate(result);
                result.write(", ");
                Expressions.getGenerator(scopeUnit, range.upper()).generate(result);
                result.write(")");
            }
        } else {
            // TODO handle BITSET
            throw new CompilerException(setExpression, "Unhandled type {0}", type);
        }
    }

}
