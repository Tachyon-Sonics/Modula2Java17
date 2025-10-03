package ch.pitchtech.modula.converter.generator.expression;

import java.util.List;

import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.runtime.Runtime;


public class ArrayAccessGenerator extends Generator {
    
    private final ArrayAccess arrayAccess;

    
    public ArrayAccessGenerator(IHasScope scopeUnit, ArrayAccess arrayAccess) {
        super(scopeUnit, arrayAccess);
        this.arrayAccess = arrayAccess;
    }
    
    public static boolean isStringAccess(ResultContext result, ArrayAccess arrayAccess) {
        IExpression arrayExpr = arrayAccess.getArray();
        IType arrayType0 = result.resolveType(arrayExpr);
        if (!(arrayType0 instanceof IArrayType))
            throw new CompilerException(arrayAccess, "Not an array");
        
        IType elementType = arrayType0;
        while (elementType instanceof IArrayType arrayType) {
            elementType = result.resolveType(arrayType.getElementType());
        }
        if (TypeHelper.isElementTypeCharArrayAsString(elementType, result)) {
            return true;
        }
        return false;
    }
    
    @Override
    public void generate(ResultContext result) {
        IExpression arrayExpr = arrayAccess.getArray();
        IType type = result.resolveType(arrayExpr);
        if (!(type instanceof IArrayType arrayType))
            throw new CompilerException(arrayAccess, "Not an array");
        ResultContext arrayContext = result.subContext();
        Expressions.getGenerator(scopeUnit, arrayExpr).generate(arrayContext);

        if (isStringAccess(result, arrayAccess)) {
            generateStringAccess(arrayContext, arrayExpr, arrayType, result);
            return;
        }
        
        result.write(arrayContext);
        writeArrayIndexes(scopeUnit, arrayType, arrayAccess.getIndexes(), result);
    }

    private void generateStringAccess(ResultContext arrayContext, IExpression arrayExpr, IArrayType arrayType, ResultContext result) {
        // Head
        List<IExpression> indexes = arrayAccess.getIndexes();
        List<IExpression> headIndexes = indexes.subList(0, indexes.size() - 1);
        writeArrayIndexes(scopeUnit, arrayType, headIndexes, arrayContext);
        
        // Last
        ResultContext lowerContext = generateLowerBound(scopeUnit, arrayType, arrayContext);
        IExpression indexExpr = arrayAccess.getIndexes().get(arrayAccess.getIndexes().size() - 1);
        result.ensureJavaImport(Runtime.class);
        
        // Check if we have a Ref<String>
        VariableDefinition arrayVariableByRef = null;
        if (headIndexes.isEmpty()) {
            arrayVariableByRef = TypeHelper.getVariableIfStringByRef(arrayExpr, scopeUnit, result);
        }

        if (arrayVariableByRef != null) {
            // Rewrite array by reference (as an IRef)
            ResultContext byRefContext = result.subContext();
            arrayVariableByRef.asReference(() -> {
                Expressions.getGenerator(scopeUnit, arrayExpr).generate(byRefContext);
            });
            arrayContext = byRefContext;
        }
        result.write("Runtime.getChar(");
        result.write(arrayContext);
        result.write(", ");
        writeArrayIndex(scopeUnit, lowerContext, result, indexExpr);
        result.write(")");
    }
    
    /**
     * Write one or more array indexes in "[" and "]", taking care of lower bounds if any.
     * @param arrayType the array type before the first specified index
     * @return the arrayType after the last index (if not all indexes were supplied), or <tt>null</tt> if all indexes
     * were supplied
     */
    public static IArrayType writeArrayIndexes(IHasScope scopeUnit, IArrayType arrayType, List<IExpression> indexes,
            ResultContext arrayContext) {
        for (IExpression indexExpr : indexes) {
            ResultContext lowerContext = generateLowerBound(scopeUnit, arrayType, arrayContext);
            arrayContext.write("[");
            writeArrayIndex(scopeUnit, lowerContext, arrayContext, indexExpr);
            arrayContext.write("]");

            IType nextType = arrayContext.resolveType(arrayType.getElementType());
            if (nextType instanceof IArrayType nextArrayType)
                arrayType = nextArrayType;
            else
                arrayType = null;
        }
        return arrayType;
    }

    public static ResultContext generateLowerBound(IHasScope scopeUnit, IArrayType arrayType, ResultContext context) {
        if (arrayType == null)
            throw new CompilerException(arrayType, "Array Type mismatch");
        ResultContext lowerContext = null;
        if (arrayType instanceof ArrayType stdArrayType) {
            IExpression lowerBound = stdArrayType.getLowerBound();
            IType lowerType = context.resolveType(lowerBound);
            lowerContext = context.subContext();
            lowerContext.pushRequestedReturnType(lowerBound, new LiteralType(BuiltInType.getTypeForJavaInt()));
            Expressions.getGenerator(scopeUnit, lowerBound).generate(lowerContext);
            if (lowerType instanceof EnumerationType enumerationType) {
                lowerContext.write(".ordinal()");
                // Detect if first member of enumeration
                if (lowerBound instanceof Identifier identifier 
                        && identifier.getName().equals(enumerationType.getElements().get(0))) {
                    lowerContext = null; // No need for a lower bound
                }
            } else if (lowerType.isBuiltInType(BuiltInType.BOOLEAN)) {
                lowerContext = processBooleanIndex(lowerBound, true, lowerContext);
            }
        }
        return lowerContext;
    }

    public static void writeArrayIndex(IHasScope scopeUnit, ResultContext lowerContext, ResultContext boundsContext, IExpression indexExpr) {
        IType indexType = boundsContext.resolveType(indexExpr);
        ResultContext afterIndexContext = boundsContext.subContext();
        if (indexType instanceof LiteralType literalType && literalType.isBuiltIn()) {
            BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
            if (builtInType.getJavaType().equals("long")) {
                boundsContext.write("(int) "); // Cast to int TODO (1) same for RangeSet, incl, etc. See CompileGrotte with 32-64 model
                if (indexExpr.isComplex(boundsContext)) {
                    boundsContext.write("(");
                    afterIndexContext.write(")");
                }
            }
        }
        
        ResultContext indexContext = boundsContext.subContext();
        Expressions.getGenerator(scopeUnit, indexExpr).generate(indexContext);
        if (indexType instanceof EnumerationType) {
            indexContext.write(".ordinal()");
        } else if (indexType.isBuiltInType(BuiltInType.BOOLEAN)) {
            indexContext = processBooleanIndex(indexExpr, false, indexContext);
        }
        
        if (lowerContext == null) { // Open array. Always starts at 0. Just write the index
            boundsContext.write(indexContext);
        } else if (indexContext.toString().matches("\\d+") && lowerContext.toString().matches("\\d+")) {
            // Two constants -> evaluate
            int index = Integer.parseInt(indexContext.toString());
            int lower = Integer.parseInt(lowerContext.toString());
            boundsContext.write(String.valueOf(index - lower));
        } else {
            boundsContext.write(indexContext);
            if (!lowerContext.toString().equals("0")) {
                boundsContext.write(" - ");
                boundsContext.write(lowerContext);
            }
        }
        boundsContext.write(afterIndexContext);
    }

    private static ResultContext processBooleanIndex(IExpression indexExpr, boolean lowerBound, ResultContext indexContext) {
        ResultContext wrapContext = indexContext.subContext();
        Object indexValue = indexExpr.evaluateConstant();
        if (indexValue instanceof Boolean lowerBoolean) {
            if (lowerBound && !lowerBoolean)
                return null; // Lower bound is false - no need to generate it
            wrapContext.write(lowerBoolean ? "1" : "0");
        } else {
            wrapContext.ensureJavaImport(Runtime.class);
            wrapContext.write("Runtime.ord(");
            wrapContext.write(indexContext);
            wrapContext.write(")");
        }
        indexContext = wrapContext;
        return indexContext;
    }

}
