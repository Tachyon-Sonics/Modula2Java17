package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.type.ArrayTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.EnumerationTypeGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.ConstantDefinition;
import ch.pitchtech.modula.converter.model.block.IDefinition;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.ArrayType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.OpenArrayType;
import ch.pitchtech.modula.converter.model.type.TypeDefinition;
import ch.pitchtech.modula.runtime.Runtime;


public class BuiltInFunctionCallGenerator extends Generator {
    
    private final FunctionCall functionCall;
    private final BuiltInProcedure builtInProc;

    
    public BuiltInFunctionCallGenerator(IHasScope scopeUnit, FunctionCall functionCall, BuiltInProcedure builtInProc) {
        super(scopeUnit, functionCall);
        this.functionCall = functionCall;
        this.builtInProc = builtInProc;
    }

    @Override
    public void generate(ResultContext result) {
        generate(result, null);
    }

    public void generate(ResultContext result, IType expectedReturnType) {
        switch (builtInProc) {
            case MIN, MAX -> generateMinOrMax(result);
            case NOT -> generateNot(result);
            case SIZE, TSIZE -> generateSize(result);
            case ORD -> generateOrd(result, expectedReturnType);
            case HIGH -> generateHigh(result);
            case SHIFT -> generateShift(result);
            case ODD -> generateOdd(result);
            case CHR -> generateChr(result);
            case ABS -> generateAbs(result);
            case ADR -> generateAdr(result);
            case VAL -> generateVal(result);
            default -> throw new CompilerException(functionCall, "Unhandled " + String.valueOf(builtInProc));
        }
    }

    private void generateMinOrMax(ResultContext result) {
        Identifier argument = (Identifier) functionCall.getArguments().get(0);
        TypeDefinition typeDefinition = result.getScope().resolveType(argument.getName());
        if (typeDefinition == null)
            throw new CompilationException(functionCall, "Cannot resolve type: {0}", argument.getName());
        IType argumentType = result.resolveType(typeDefinition.getType());
        argumentType = result.resolveType(argumentType);
        if (argumentType instanceof LiteralType literalType) {
            if (literalType.isBuiltIn()) {
                BuiltInType bType = BuiltInType.valueOf(literalType.getName());
                String javaValue;
                if (builtInProc == BuiltInProcedure.MIN) {
                    javaValue = switch (bType) {
                        case INTEGER -> "Short.MIN_VALUE";
                        case SHORTINT -> "Byte.MIN_VALUE";
                        case LONGINT -> "Integer.MIN_VALUE";
                        case CARDINAL, SHORTCARD, LONGCARD -> "0";
                        case BOOLEAN -> "false";
                        default -> throw new CompilerException(functionCall, "Unhandled " + String.valueOf(bType));
                    };
                } else {
                    javaValue = switch (bType) {
                        case INTEGER -> "Short.MAX_VALUE";
                        case SHORTINT -> "Byte.MAX_VALUE";
                        case LONGINT -> "Integer.MAX_VALUE";
                        case CARDINAL -> "((1 << 16) - 1)";
                        case SHORTCARD -> "((1 << 8) - 1)";
                        case LONGCARD -> "((1L << 32) - 1)";
                        case BOOLEAN -> "true";
                        default -> throw new CompilerException(functionCall, "Unhandled " + String.valueOf(bType));
                    };
                }
                String comment = " /* " + builtInProc.name() + "(" + bType.name() + ")" + " */";
                result.write(javaValue + comment);
            } else {
                throw new CompilerException(functionCall, "Unhandled " + String.valueOf(literalType));
            }
        } else if (argumentType instanceof EnumerationType enumerationType) {
            boolean useInteger = false;
            IType requestedType = result.popRequestedReturnType(functionCall);
            if (requestedType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                BuiltInType biType = BuiltInType.valueOf(literalType.getName());
                if (biType.isNumeric() && !biType.isDecimal())
                    useInteger = true;
            }
            
            if (builtInProc == BuiltInProcedure.MIN) {
                if (useInteger) {
                    result.write("0");
                } else {
                    Identifier firstItem = new Identifier(functionCall.getSourceLocation(), scopeUnit, 
                            enumerationType.getElements().get(0));
                    new IdentifierGenerator(scopeUnit, scopeUnit.getCompilationUnit(), firstItem).generate(result);
                    result.write(" /* MIN(" + enumerationType.getName() + ") */");
                }
            } else { // MAX
                if (useInteger) {
                    new EnumerationTypeGenerator(scopeUnit, enumerationType).generate(result);
                    result.write(".values().length - 1");
                } else {
                    Identifier lastItem = new Identifier(functionCall.getSourceLocation(), scopeUnit, 
                            enumerationType.getElements().get(enumerationType.getElements().size() - 1));
                    new IdentifierGenerator(scopeUnit, scopeUnit.getCompilationUnit(), lastItem).generate(result);
                    result.write(" /* MAX(" + enumerationType.getName() + ") */");
                }
            }
        } else {
            throw new CompilerException(functionCall, "Unhandled "+ String.valueOf(argumentType));
        }
    }
    
    private void generateNot(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expr = functionCall.getArguments().get(0);
        boolean complex = expr.isComplex(result);
        result.write("!");
        if (complex)
            result.write("(");
        Expressions.getGenerator(scopeUnit, expr).generate(result);
        if (complex)
            result.write(")");
    }

    private void generateSize(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        result.ensureJavaImport(Runtime.class);
        
        /*
         * In general, SIZE(Type) is used for memory management.
         * Here we convert SIZE(Type) to Runtime.sizeOf(calculated_size, Type.class) so it is at least possible to implement
         * stuff such as creating new instances in Java. Runtime.sizeOf() will store the class, and return the calculated size.
         * The stored class is what is needed to implement Storage.ALLOCATE for instance.
         */
        
        IType argumentType;
        if (functionCall.getArguments().get(0) instanceof Identifier argument) {
            // we allow variable, type or constant because this method handles both SIZE() and TSIZE()
            IDefinition definition = result.getScope().resolve(argument.getName(), true, true, true, false);
            if (definition == null)
                throw new CompilationException(functionCall, "Cannot resolve type: {0}", argument.getName());
            if (definition instanceof TypeDefinition typeDefinition)
                argumentType = typeDefinition.getType();
            else if (definition instanceof ConstantDefinition constantDefinition)
                argumentType = constantDefinition.getValue().getType(result.getScope());
            else if (definition instanceof VariableDefinition variableDefinition)
                argumentType = variableDefinition.getType();
            else
                throw new CompilationException(functionCall, "Unhandled: {0}", definition);
        } else {
            // This is an expression - get its type
            IExpression argument = functionCall.getArguments().get(0);
            argumentType = result.resolveType(argument);
        }
        
        argumentType = result.resolveType(argumentType);
        int calculatedSize = SizeCalculator.getJavaSizeOf(functionCall, result, argumentType);
        result.write("Runtime.sizeOf(");
        result.write(String.valueOf(calculatedSize));
        result.write(", ");

        if (argumentType instanceof ArrayType) {
            // Bounds
            int nbBounds = 0;
            ResultContext boundsContext = result.subContext();
            IType currentType = argumentType;
            while (currentType instanceof ArrayType arrayType) {
                nbBounds++;
                boundsContext.write(", ");
                
                IExpression upperBound = arrayType.getUpperBound();
                IExpression lowerBound = arrayType.getLowerBound();
                ResultContext upperContext = boundsContext.subContext();
                ResultContext lowerContext = boundsContext.subContext();
                Expressions.getGenerator(scopeUnit, upperBound).generate(upperContext);
                Expressions.getGenerator(scopeUnit, lowerBound).generate(lowerContext);
                ArrayTypeGenerator.writeArrayBound(boundsContext, upperContext.toString(), lowerContext.toString());
                
                currentType = result.resolveType(arrayType.getElementType());
            }
            String boundsStr = boundsContext.toString();
            
            // Type
            if (currentType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
                result.write("String");
                
                // Drop last bound
                nbBounds--;
                if (nbBounds == 0) {
                    boundsStr = "";
                } else {
                    int lastSep = boundsStr.lastIndexOf(',');
                    boundsStr = boundsStr.substring(0, lastSep);
                }
            } else {
                Types.getGenerator(scopeUnit, currentType).generate(result);
            }
            for (int i = 0; i < nbBounds; i++)
                result.write("[]");
            result.write(".class");
            result.write(boundsStr);
        } else {
            Types.getGenerator(scopeUnit, argumentType).generate(result);
            result.write(".class");
        }
        
        result.write(")");
    }

    private void generateOrd(ResultContext result, IType expectedReturnType) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        ResultContext exprContext = result.subContext();
        
        // Handle any unboxing
        VariableDefinition byRefVariableDefinition = TypeHelper.getVariableIfByRef(expression, scopeUnit, result);
        if (byRefVariableDefinition != null) {
            assert byRefVariableDefinition.isUseRef();
            // Ref<Integer>.value -> cast to (int)
            LiteralType literalType = (LiteralType) result.resolveType(expression);
            BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
            String javaPrimitiveType = builtInType.getJavaType();
            exprContext.write("(" + javaPrimitiveType + ") ");
        }
        
        Expressions.getGenerator(scopeUnit, expression).generate(exprContext);
        
        IType argumentType = expression.getType(result.getScope());
        argumentType = result.resolveType(argumentType);
        if (argumentType instanceof LiteralType literalType) {
            if (literalType.isBuiltIn()) {
                BuiltInType bType = BuiltInType.valueOf(literalType.getName());
                String javaValue = switch (bType) {
                    case BOOLEAN -> {
                        String exprString = exprContext.toString();
                        if (exprString.equals("false")) {
                            yield "0 /* ORD(FALSE) */";
                        } else if (exprString.equals("true")) {
                            yield "1 /* ORD(TRUE) */";
                        } else {
                            yield "(" + exprString + " ? 1 : 0)";
                        }
                    }
                    case INTEGER, SHORTINT, LONGINT, CARDINAL, SHORTCARD, LONGCARD -> exprContext.toString();
                    case STRING -> {
                        if (expression instanceof StringLiteral stringLiteral) {
                            String text = stringLiteral.getText();
                            if (text.length() != 1)
                                throw new CompilationException(expression, "Char (String of length 1) expected");
                            yield "'" + text.replace("\\", "\\\\").replace("'", "\\'") + "'";
                        } else {
                            String exprString = exprContext.toString();
                            yield exprString + ".charAt(0)";
                        }
                    }
                    case CHAR -> exprContext.toString();
                    default -> throw new CompilerException(functionCall, "Unhandled " + String.valueOf(bType));
                };
                result.write(javaValue);
            } else {
                throw new CompilerException(functionCall, "Unhandled " + String.valueOf(literalType));
            }
        } else if (argumentType instanceof EnumerationType) {
            result.write(exprContext);
            result.write(".ordinal()"); // While we could evaluate if a constant, it would make the Java code less clear
        } else {
            throw new CompilerException(functionCall, "Unhandled " + String.valueOf(argumentType));
        }
    }
    
    private void generateHigh(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        IType type = result.resolveType(expression);
        if (!(type instanceof OpenArrayType openArrayType))
            throw new CompilationException(functionCall, "HIGH only allowed on open array type. Found: {0}", type);
        IType elementType = result.resolveType(openArrayType.getElementType());
        
        result.write("(");
        if (expression.isComplex(result))
            result.write("(");
        Expressions.getGenerator(scopeUnit, expression).generate(result);
        if (expression.isComplex(result))
            result.write(")");
        if (elementType.isBuiltInType(BuiltInType.CHAR) && result.getCompilerOptions().isConvertArrayOfCharToString()) {
            result.write(".length() - 1)");
        } else {
            result.write(".length - 1)");
        }
    }
    
    private void generateShift(ResultContext result) {
        if (functionCall.getArguments().size() != 2)
            throw new CompilationException(functionCall, "Expected number of arguments: 2, found {0}", functionCall.getArguments().size());
        IExpression expr = functionCall.getArguments().get(0);
        IExpression shift = functionCall.getArguments().get(1);
        IType exprType = result.resolveType(expr);
        boolean signed = true;
        boolean negative = false;
        if (exprType instanceof LiteralType literalType && literalType.isBuiltIn()) {
            BuiltInType biType = BuiltInType.valueOf(literalType.getName());
            if (biType.isNumeric() && !biType.isSigned())
                signed = false;
        }
        if (shift instanceof MinusExpression minusExpr) {
            shift = minusExpr.getTarget();
            negative = true;
        }
        
        result.write("(");
        if (expr.isComplex(result))
            result.write("(");
        Expressions.getGenerator(scopeUnit, expr).generate(result);
        if (expr.isComplex(result))
            result.write(")");
        
        if (!negative)
            result.write(" << ");
        else if (signed)
            result.write(" >> ");
        else
            result.write(" >>> ");
        
        if (shift.isComplex(result))
            result.write("(");
        Expressions.getGenerator(scopeUnit, shift).generate(result);
        if (shift.isComplex(result))
            result.write(")");
        result.write(")");
    }
    
    private void generateOdd(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        ResultContext exprContext = result.subContext();
        
        result.write("((");
        Expressions.getGenerator(scopeUnit, expression).generate(exprContext);
        if (expression.isComplex(result))
            result.write("(");
        result.write(exprContext);
        if (expression.isComplex(result))
            result.write(")");
        result.write(" % 2) != 0)");
    }
    
    private void generateChr(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        ResultContext exprContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression).generate(exprContext);
        
        result.write("(char) ");
        if (expression.isComplex(result))
            result.write("(");
        result.write(exprContext);
        if (expression.isComplex(result))
            result.write(")");
    }

    private void generateAbs(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        ResultContext exprContext = result.subContext();
        Expressions.getGenerator(scopeUnit, expression).generate(exprContext);
        
        result.write("Math.abs(");
        result.write(exprContext);
        result.write(")");
    }
    
    private void generateAdr(ResultContext result) {
        if (functionCall.getArguments().size() != 1)
            throw new CompilationException(functionCall, "Expected number of arguments: 1, found {0}", functionCall.getArguments().size());
        IExpression expression = functionCall.getArguments().get(0);
        VariableDefinition variableDefinition = TypeHelper.getVariableIfByRef(expression, scopeUnit, result);
        if (variableDefinition != null) {
            // ADR(x) where x is a Ref -> just use the ref itself
            variableDefinition.asReference(() -> {
                Expressions.getGenerator(scopeUnit, expression).generate(result);
            });
            return;
        }
        
        // Record, array: already references in Java
        Expressions.getGenerator(scopeUnit, expression).generate(result);
    }
    
    private void generateVal(ResultContext result) {
        if (functionCall.getArguments().size() != 2)
            throw new CompilationException(functionCall, "Expected number of arguments: 2, found {0}", functionCall.getArguments().size());
        IExpression typeExpr = functionCall.getArguments().get(0);
        IExpression valueExpr = functionCall.getArguments().get(1);
        if (typeExpr instanceof Identifier typeIdentifier) {
            IType type = result.resolveType(new LiteralType(typeIdentifier.getSourceLocation(), scopeUnit, typeIdentifier.getName(), false));
            if (type instanceof EnumerationType) {
                // Assume numeric to enum
                Types.getGenerator(scopeUnit, type).generate(result);
                result.write(".values()[");
                Expressions.getGenerator(scopeUnit, valueExpr).generate(result);
                result.write("]");
            } else {
                // Generate type cast
                result.write("(");
                Types.getGenerator(scopeUnit, type).generate(result);
                result.write(") ");
                if (valueExpr.isComplex(result))
                    result.write("(");
                Expressions.getGenerator(scopeUnit, valueExpr).generate(result);
                if (valueExpr.isComplex(result))
                    result.write(")");
            }
        } else {
            throw new CompilationException(functionCall, "Type expected");
        }
    }
}
