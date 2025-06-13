package ch.pitchtech.modula.converter.generator.type;

import java.util.ArrayList;
import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpaqueType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.model.type.RecordType;

// TODO Z test ARRAY OF ARRAY OF RECORD/SET
public class ArrayTypeGenerator extends Generator implements ITypePreInitializerGenerator {
    
    private final ArrayType arrayType;

    
    public ArrayTypeGenerator(IHasScope scopeUnit, ArrayType arrayType) {
        super(scopeUnit, arrayType);
        this.arrayType = arrayType;
    }

    @Override
    public void generate(ResultContext result) {
        IType elementType0 = arrayType.getElementType();
        IType elementType = result.resolveType(elementType0);
        if (elementType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
            result.write("String");
        } else {
            Types.getGenerator(scopeUnit, elementType).generate(result);
            result.write("[]");
        }
    }

    @Override
    public void generateInitializer(ResultContext before, ResultContext init, boolean force) {
        ResultContext result = init.subContext();
        boolean suppressWarnings = false;
        IType elementType = result.resolveType(arrayType.getElementType());
        while (elementType instanceof ArrayType subArrayType)
            elementType = result.resolveType(subArrayType.getElementType());
        
        if (elementType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
            init.write("\"\"");
        } else if (elementType instanceof PointerType || elementType instanceof OpaqueType || elementType instanceof ProcedureType) {
            suppressWarnings = generateInitializer(result, elementType);
        } else if (elementType instanceof LiteralType literalType) {
            generateInitializer(result, literalType);
        } else {
            generateComplexInitializer(result, elementType);
        }
        
        if (suppressWarnings)
            before.writeLine("@SuppressWarnings(\"unchecked\")");
        init.write(result);
    }
    
    private boolean generateComplexInitializer(ResultContext result, IType elementType) {
        ResultContext initContext = result.subContext();
        boolean suppressWarnings = generateInitializerExpr(initContext, elementType);
        
        result.ensureJavaImport(ch.pitchtech.modula.runtime.Runtime.class);
        result.write("Runtime.initArray(");
        result.write(initContext);
        if (elementType instanceof RecordType) {
            /*
             * The folowing is not necessary, because Runtime.initArray() defaults to using the empty constructor,
             * which is always valid for creating a record.
             * However, we could uncomment it if we want to prevent introspection of the empty constructor,
             * resulting is slightly more complex calling code.
             */
//            result.write(", ");
//            new RecordTypeGenerator(scopeUnit, recordType).generate(result);
//            result.write("::new");
        } else {
            Generator itemGenerator = Types.getGenerator(scopeUnit, elementType);
            if (itemGenerator instanceof ITypePreInitializerGenerator preInitializerGenerator) {
                ResultContext itemInitContext = result.subContext();
                ResultContext itemBeforeContext = result.subContext();
                preInitializerGenerator.generateInitializer(itemBeforeContext, itemInitContext, false);
                String javaInit = itemInitContext.toString();
                if (!javaInit.isBlank()) {
                    result.write(", ");
                    result.write("() -> " + javaInit);
                    if (!itemBeforeContext.toString().isBlank())
                        suppressWarnings = true;
                }
            }
        }
        result.write(")");
        return suppressWarnings;
    }
    
    private static record Bounds(String lowerBound, String upperBound) {}

    private boolean generateInitializer(ResultContext result, IType elementType) {
        return generateInitializerExpr(result, elementType);
    }
    
    private boolean generateInitializerExpr(ResultContext result, IType elementType) {
        boolean suppressWarnings = false;
        
        List<Bounds> bounds = new ArrayList<>();
        IType currentType = arrayType;
        while (currentType instanceof ArrayType currentArrayType) {
            ResultContext upperBoundContext = result.subContext();
            ResultContext lowerBoundContext = result.subContext();
            IExpression upperBoundExpr = currentArrayType.getUpperBound();
            upperBoundContext.pushRequestedReturnType(upperBoundExpr, new LiteralType(BuiltInType.javaInt()));
            Expressions.getGenerator(scopeUnit, upperBoundExpr).generate(upperBoundContext);
            IExpression lowerBoundExpr = currentArrayType.getLowerBound();
            lowerBoundContext.pushRequestedReturnType(lowerBoundExpr, new LiteralType(BuiltInType.javaInt()));
            Expressions.getGenerator(scopeUnit, lowerBoundExpr).generate(lowerBoundContext);
            String lowerBound = lowerBoundContext.toString();
            String upperBound = upperBoundContext.toString();
            IType lowerType = result.resolveType(lowerBoundExpr);
            lowerBound = processBound(result, lowerBoundExpr, lowerBound, lowerType);
            IType upperType = result.resolveType(upperBoundExpr);
            upperBound = processBound(result, upperBoundExpr, upperBound, upperType);
            bounds.add(new Bounds(lowerBound, upperBound));
            
            currentType = result.resolveType(currentArrayType.getElementType());
        }
        
        result.write("new ");
        ResultContext elementResult = result.subContext();
        while (elementType instanceof ArrayType arrayType)
            elementType = result.resolveType(arrayType.getElementType());
        if (elementType instanceof LiteralType literalType)
            new LiteralTypeGenerator(scopeUnit, literalType).generate(elementResult);
        else if (elementType instanceof PointerType pointerType)
            suppressWarnings = new PointerTypeGenerator(scopeUnit, pointerType).generate(elementResult, true);
        else
            Types.getGenerator(scopeUnit, elementType).generate(elementResult);
        String elementStr = elementResult.toString();
        String elementSuffix = "";
        
        // Extract any open array, because we need to move them after those with a bound specified
        while (elementStr.endsWith("[]")) {
            elementSuffix += "[]";
            elementStr = elementStr.substring(0, elementStr.length() - "[]".length());
        }
        result.write(elementStr);
        
        for (Bounds bound : bounds) {
            String upperBound = bound.upperBound();
            String lowerBound = bound.lowerBound();
            result.write("[");
            writeArrayBound(result, upperBound, lowerBound);
            result.write("]");
        }
        result.write(elementSuffix);
        return suppressWarnings;
    }

    private String processBound(ResultContext result, IExpression boundExpr, String bound, IType boundType) {
        if (boundType instanceof EnumerationType) {
            bound += ".ordinal()";
        } else if (boundType.isBuiltInType(BuiltInType.BOOLEAN)) {
            Object boundValue = boundExpr.evaluateConstant();
            if (boundValue instanceof Boolean boundBooleanValue) {
                bound = (boundBooleanValue ? "1" : "0");
            } else {
                result.ensureJavaImport(ch.pitchtech.modula.runtime.Runtime.class);
                bound = "Runtime.ord(" + bound + ")";
            }
        }
        return bound;
    }

    public static void writeArrayBound(ResultContext result, String upperBound, String lowerBound) {
        boolean oneAdded = false;
        if (upperBound.toString().endsWith(" - 1")) {
            upperBound = upperBound.substring(0, upperBound.length() - " - 1".length());
            oneAdded = true;
        }
        Integer upperValue = getIfNumber(upperBound);
        if (upperValue != null && !oneAdded && lowerBound.equals("0")) {
            upperBound = String.valueOf(upperValue + 1);
            oneAdded = true;
        }
   
        if (upperBound.contains(" "))
            result.write("(");
        result.write(upperBound);
        if (upperBound.contains(" "))
            result.write(")");
        if (lowerBound.equals("1") && !oneAdded) {
            oneAdded = true; // ARRAY [1..15] -> "15" instead of "15 - 1 + 1"
        } else if (!lowerBound.equals("0")) {
            result.write(" - ");
            if (lowerBound.contains(" "))
                result.write("(");
            result.write(lowerBound);
            if (lowerBound.contains(" "))
                result.write(")");
        }
        if (!oneAdded)
            result.write(" + 1");
    }
    
    private static Integer getIfNumber(String text) {
        try {
            if (text.matches("\\d+"))
                return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            // Ignore - not a number
        }
        return null;
    }
    
}
