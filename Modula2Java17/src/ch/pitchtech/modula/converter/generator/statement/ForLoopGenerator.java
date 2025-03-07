package ch.pitchtech.modula.converter.generator.statement;

import java.util.List;

import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.expression.Expressions;
import ch.pitchtech.modula.converter.generator.expression.IdentifierGenerator;
import ch.pitchtech.modula.converter.generator.type.Types;
import ch.pitchtech.modula.converter.model.block.VariableDefinition;
import ch.pitchtech.modula.converter.model.builtin.BuiltInProcedure;
import ch.pitchtech.modula.converter.model.builtin.BuiltInType;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.statement.Assignement;
import ch.pitchtech.modula.converter.model.statement.ForLoop;
import ch.pitchtech.modula.converter.model.statement.IStatement;
import ch.pitchtech.modula.converter.model.statement.ProcedureCall;
import ch.pitchtech.modula.converter.model.type.EnumerationType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;


public class ForLoopGenerator extends Generator {
    
    private final ForLoop forLoop;

    
    public ForLoopGenerator(IHasScope scopeUnit, ForLoop forLoop) {
        super(scopeUnit, forLoop);
        this.forLoop = forLoop;
    }

    @Override
    public void generate(ResultContext result) { // TODO (3) simplify loop over enum with 1-increment using enhanced for loop
        String variableName = forLoop.getIdentifier().getName();
        VariableDefinition variableDefinition = scopeUnit.getScope().resolveVariable(variableName);
        IType variableType = result.resolveType(variableDefinition.getType());
        if (variableType instanceof EnumerationType enumerationType) {
            generateEnumerated(result, variableName, variableDefinition, enumerationType);
            return;
        }
        
        result.writeIndent();
        result.write("for (");
        Assignement forAssignment = new Assignement(forLoop.getSourceLocation(), forLoop.getIdentifier(), forLoop.getFromExpression());
        new AssignmentGenerator(scopeUnit, forAssignment).generate(result, true);
        result.write("; ");
        
        IExpression byExpression = forLoop.getByExpression();

        new IdentifierGenerator(scopeUnit, scopeUnit.getCompilationUnit(), forLoop.getIdentifier()).generate(result);
        ResultContext toContext = result.subContext();
        Expressions.getGenerator(scopeUnit, forLoop.getToExpression()).generate(toContext);
        if (byExpression != null) {
            Object byValue = byExpression.evaluateConstant();
            if (byValue == null) {
                /*
                 * Unable to evaluate "BY" as a constant expression. Because we do not know if it is positive or negative,
                 * use "!=". Because "TO" value is inclusive, stop at "TO + BY".
                 */
                result.write(" != (");
                result.write(toContext);
                result.write(") + (");
                Expressions.getGenerator(scopeUnit, byExpression).generate(result);
                result.write(")");
            } else {
                if (byValue instanceof Number number && number.longValue() < 0)
                    result.write(" >= ");
                else
                    result.write(" <= ");
                result.write(toContext);
            }
        } else if (toContext.toString().endsWith(" - 1")) {
            // " <= xxx - 1 " -> " < xxx "
            String toExpr = toContext.toString();
            toExpr = toExpr.substring(0, toExpr.length() - " - 1".length());
            result.write(" < ");
            result.write(toExpr);
        } else {
            result.write(" <= ");
            result.write(toContext);
        }
        result.write("; ");
        
        if (byExpression != null) {
            if (byExpression instanceof MinusExpression minusExpression) {
                // Simplify " += -<expr>" into "-= <expr>"
                IExpression posExpression = minusExpression.getTarget();
                ProcedureCall incCall = new ProcedureCall(forLoop.getSourceLocation(), scopeUnit, BuiltInProcedure.DEC.name());
                incCall.addArguments(List.of(forLoop.getIdentifier(), posExpression));
                new BuiltInProcedureCallGenerator(scopeUnit, incCall, BuiltInProcedure.DEC).generateIncOrDec(result, true);
            } else {
                ProcedureCall incCall = new ProcedureCall(forLoop.getSourceLocation(), scopeUnit, BuiltInProcedure.INC.name());
                incCall.addArguments(List.of(forLoop.getIdentifier(), byExpression));
                new BuiltInProcedureCallGenerator(scopeUnit, incCall, BuiltInProcedure.INC).generateIncOrDec(result, true);
            }
        } else {
            ProcedureCall incCall = new ProcedureCall(forLoop.getSourceLocation(), scopeUnit, BuiltInProcedure.INC.name());
            incCall.addArguments(List.of(forLoop.getIdentifier()));
            new BuiltInProcedureCallGenerator(scopeUnit, incCall, BuiltInProcedure.INC).generateIncOrDec(result, true);
        }
        
        result.write(") {");
        result.writeLn();
        
        result.incIndent();
        for (IStatement statement : forLoop.getStatements())
            Statements.getGenerator(scopeUnit, statement).generate(result);
        result.decIndent();
        result.writeLine("}");
    }
    
    /**
     * FOR variable is of an enumerated type
     * <p>
     * We cannot use an enumerated variable because Runtime.next() might be called on the last enum member and fail.
     * We hence use an int helper variable prefixed with "<tt>_</tt>"
     */
    private void generateEnumerated(ResultContext result, String variableName, VariableDefinition variableDefinition,
            EnumerationType varType) {
        result.writeIndent();
        result.write("for (");
        
        IExpression fromExpression = forLoop.getFromExpression();
        boolean isMin = (fromExpression instanceof FunctionCall functionCall && functionCall.isBuiltIn(BuiltInProcedure.MIN));
        IType fromType = result.resolveType(fromExpression);
        result.write("int _" + variableName);
        result.write(" = ");
        ResultContext fromContext = result.subContext();
        IType expectedType = new LiteralType(BuiltInType.javaInt());
        fromContext.pushRequestedReturnType(fromExpression, expectedType);
        Expressions.getGenerator(scopeUnit, fromExpression, expectedType).generate(fromContext);
        if (fromType instanceof EnumerationType) {
            result.write(fromContext);
            result.write(".ordinal()");
        } else if (fromContext.toString().equals("0") && isMin) {
            // MIN(enumType) -> just use "0"
            result.write("0");
        } else if (fromContext.toString().matches("\\d+")) {
            // Numeric constant
            int valueIndex = Integer.parseInt(fromContext.toString());
            Types.getGenerator(scopeUnit, varType).generate(result);
            result.write(".");
            result.write(varType.getElements().get(valueIndex));
            result.write(".ordinal()");
        } else {
            // Assume numeric expression type
            result.write(fromContext);
        }
        result.write("; ");
        
        ResultContext toContext = result.subContext();
        toContext.pushRequestedReturnType(forLoop.getToExpression(), new LiteralType(BuiltInType.javaInt()));
        Expressions.getGenerator(scopeUnit, forLoop.getToExpression()).generate(toContext);
        result.write("_" + variableName);
        IType toType = result.resolveType(forLoop.getToExpression());
        if (toContext.toString().endsWith(" - 1") && !(toType instanceof EnumerationType)) {
            result.write(" < ");
            String toExpr = toContext.toString();
            toExpr = toExpr.substring(0, toExpr.length() - " - 1".length());
            result.write(toExpr);
        } else {
            result.write(" <= ");
            result.write(toContext);
        }
        if (toType instanceof EnumerationType)
            result.write(".ordinal()");
        result.write("; ");
        
        IExpression byExpression = forLoop.getByExpression();
        if (byExpression != null) {
            /*
             * Not sure BY is allowed in a FOR loop with an enumerated variable type...
             * Assume BY is either numeric or enumerated (in which case we use the ordinal).
             * 
             * In all cases, the Java equivalent sucks...
             */
            result.write("_" + variableName);
            result.write(" += ");
            IType byType = result.resolveType(byExpression);
            Expressions.getGenerator(scopeUnit, byExpression).generate(result);
            if (byType instanceof EnumerationType)
                result.write(".ordinal()");
        } else {
            result.write("_" + variableName + "++");
        }
        
        result.write(") {");
        result.writeLn();
        
        result.incIndent();
        
        // a = Anims.values()[_a];
        result.writeIndent();
        result.write(variableName);
        result.write(" = ");
        Types.getGenerator(scopeUnit, varType).generate(result);
        result.write(".values()[");
        result.write("_" + variableName);
        result.write("];");
        result.writeLn();
        
        for (IStatement statement : forLoop.getStatements())
            Statements.getGenerator(scopeUnit, statement).generate(result);
        result.decIndent();
        result.writeLine("}");
    }

}
