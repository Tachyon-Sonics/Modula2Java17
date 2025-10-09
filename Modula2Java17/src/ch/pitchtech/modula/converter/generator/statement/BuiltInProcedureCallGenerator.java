package ch.pitchtech.modula.converter.generator.statement;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.ArrayIndexHelper;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.expression.IAssignGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.Constructor;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.type.EnumSetType;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RangeSetType;
import ch.pitchtech.modula.runtime.HaltException;
import ch.pitchtech.modula.runtime.Runtime;


public class BuiltInProcedureCallGenerator extends Generator {

    private final ProcedureCall procedureCall;
    private final BuiltInProcedure builtInProc;

    
    public BuiltInProcedureCallGenerator(IHasScope scopeUnit, ProcedureCall procedureCall, BuiltInProcedure builtInProc) {
        super(scopeUnit, procedureCall);
        this.procedureCall = procedureCall;
        this.builtInProc = builtInProc;
    }

    @Override
    public void generate(ResultContext result) {
        switch (builtInProc) {
            case INC, DEC -> generateIncOrDec(result);
            case INCL, EXCL -> generateInclOrExcl(result);
            case EXIT -> generateExit(result);
            case HALT -> generateHalt(result);
            case NEW -> generateNew(result);
            case DISPOSE -> generateDispose(result);
            default -> throw new CompilerException(procedureCall, "Unhandled " + String.valueOf(builtInProc));
        }
    }

    private void generateIncOrDec(ResultContext result) {
        generateIncOrDec(result, false);
    }
    
    public void generateIncOrDec(ResultContext result, boolean inline) {
        if (procedureCall.getArguments().size() != 1 && procedureCall.getArguments().size() != 2)
            throw new CompilationException(procedureCall, "Expected 1 or 2 arguments");
        if (!inline)
            result.writeIndent();
        IExpression targetExpr = procedureCall.getArguments().get(0);
        Generator targetGenerator = Expressions.getGenerator(scopeUnit, targetExpr);
        IType targetType = result.resolveType(targetExpr);
        try {
            VariableDefinition byRefVariableDefinition = TypeHelper.getVariableIfByRef(targetExpr, scopeUnit, result);
            if (byRefVariableDefinition != null) {
                // target is an IRef. We can use the inc() and dec() methods
                byRefVariableDefinition.asReference(() -> {
                    Expressions.getGenerator(scopeUnit, targetExpr).generate(result);
                });
                result.write((builtInProc == BuiltInProcedure.INC ? ".inc(" : ".dec("));
                if (procedureCall.getArguments().size() == 2) {
                    IExpression deltaExpr = procedureCall.getArguments().get(1);
                    Expressions.getGenerator(scopeUnit, deltaExpr).generate(result);
                }
                result.write(")");
                return;
            } else if (targetGenerator instanceof IAssignGenerator assignGenerator) {
                // Use an assignment method on "targetExpr +/- 1"
                SourceLocation srcLoc = null;
                if (targetExpr instanceof SourceElement sourceElement)
                    srcLoc = sourceElement.getSourceLocation();
                String operator = (builtInProc == BuiltInProcedure.INC ? "+" : "-");
                IExpression deltaExpr;
                if (procedureCall.getArguments().size() == 2)
                    deltaExpr = procedureCall.getArguments().get(1);
                else
                    deltaExpr = new ConstantLiteral(srcLoc, "1");
                InfixOpExpression newExpr = new InfixOpExpression(srcLoc, targetExpr, operator, deltaExpr);
                if (targetType instanceof LiteralType literalType && literalType.isBuiltIn()) {
                    BuiltInType builtInType = BuiltInType.valueOf(literalType.getName());
                    /*
                     * When invoked on CHAR, keep the resulting type as CHAR
                     */
                    if (!builtInType.isNumeric())
                        newExpr.setForceType(targetType);
                }
                
                boolean assigned = assignGenerator.generateAssignement(result, newExpr);
                if (assigned) {
                    return;
                }
            }
            if (targetType instanceof EnumerationType) {
                result.ensureJavaImport(Runtime.class);
                Expressions.getGenerator(scopeUnit, targetExpr).generate(result);
                if (builtInProc == BuiltInProcedure.INC)
                    result.write(" = Runtime.next(");
                else
                    result.write(" = Runtime.prev(");
                targetGenerator.generate(result);
                if (procedureCall.getArguments().size() == 2) {
                    result.write(", ");
                    Expressions.getGenerator(scopeUnit, procedureCall.getArguments().get(1)).generate(result);
                }
                result.write(")");
                return;
            }
    
            targetGenerator.generate(result);
            if (procedureCall.getArguments().size() == 2) {
                if (builtInProc == BuiltInProcedure.INC)
                    result.write(" += ");
                else
                    result.write(" -= ");
                Expressions.getGenerator(scopeUnit, procedureCall.getArguments().get(1)).generate(result);
            } else {
                if (builtInProc == BuiltInProcedure.INC)
                    result.write("++");
                else
                    result.write("--");
            }
        } finally {
            if (!inline) {
                result.write(";");
                result.writeLn();
            }
        }
    }
    
    private void generateInclOrExcl(ResultContext result) {
        if (procedureCall.getArguments().size() != 2)
            throw new CompilationException(procedureCall, "Expected 2 arguments");
        IExpression setExpression = procedureCall.getArguments().get(0);
        IType setType = result.resolveType(setExpression);
        IExpression itemExpression = procedureCall.getArguments().get(1);
        
        result.writeIndent();
        Expressions.getGenerator(scopeUnit, setExpression).generate(result);
        boolean incl = (builtInProc == BuiltInProcedure.INCL);
        String methodName;
        // What a lack of coherency...
        if (setType instanceof RangeSetType) {
            methodName = (incl ? "incl" : "excl");
        } else if (setType instanceof EnumSetType) {
            methodName = (incl ? "add" : "remove");
        } else { // BitSet
            methodName = (incl ? "set" : "clear");
        }
        result.write("." + methodName + "(");
        ArrayIndexHelper.writeIntIndex(scopeUnit, itemExpression, result);
        result.write(");");
        result.writeLn();
    }
    
    private void generateExit(ResultContext result) {
        result.writeLine("break;"); // TODO add a label if necessary (such as nested WITH or while)
    }
    
    private void generateHalt(ResultContext result) {
        result.ensureJavaImport(HaltException.class);
        result.writeLine("throw new HaltException();");
    }
    
    private void generateNew(ResultContext result) {
        if (procedureCall.getArguments().size() != 1) {
            throw new CompilationException(procedureCall, "Expected one argument, found: ", procedureCall.getArguments().size());
        }
        IExpression argument = procedureCall.getArguments().get(0);
        IType type = result.resolveType(argument);
        if (!(type instanceof PointerType)) {
            throw new CompilationException(procedureCall, "Argument must be of pointer type");
        }
        
        // Generate an assignment: "<expression> := <constructor>"
        Assignement assignment = new Assignement(
                procedureCall.getSourceLocation(), 
                argument, 
                new Constructor(procedureCall.getSourceLocation(), type));
        new AssignmentGenerator(scopeUnit, assignment).generate(result);
    }
    
    private void generateDispose(ResultContext result) {
        if (procedureCall.getArguments().size() != 1) {
            throw new CompilationException(procedureCall, "Expected one argument, found: ", procedureCall.getArguments().size());
        }
        IExpression argument = procedureCall.getArguments().get(0);
        IType type = result.resolveType(argument);
        if (!(type instanceof PointerType)) {
            throw new CompilationException(procedureCall, "Argument must be of pointer type");
        }
        
        // Generate an assignment: "<expression> := NIL"
        Assignement assignment = new Assignement(
                procedureCall.getSourceLocation(), 
                argument,
                new Identifier(procedureCall.getSourceLocation(), scopeUnit, "NIL"));
        new AssignmentGenerator(scopeUnit, assignment).generate(result);
    }

}
