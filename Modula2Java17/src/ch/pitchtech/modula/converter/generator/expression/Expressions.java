package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.model.ICompilationUnit;
import ch.pitchtech.modula.converter.model.expression.ArrayAccess;
import ch.pitchtech.modula.converter.model.expression.ConstantLiteral;
import ch.pitchtech.modula.converter.model.expression.Dereference;
import ch.pitchtech.modula.converter.model.expression.FieldAccess;
import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.expression.Identifier;
import ch.pitchtech.modula.converter.model.expression.InfixOpExpression;
import ch.pitchtech.modula.converter.model.expression.MinusExpression;
import ch.pitchtech.modula.converter.model.expression.ParenthesedExpression;
import ch.pitchtech.modula.converter.model.expression.SetExpression;
import ch.pitchtech.modula.converter.model.expression.StringLiteral;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IType;

public class Expressions {
    
    public static Generator getGenerator(IHasScope scopeUnit, IExpression expression) {
        return getGenerator(scopeUnit, expression, null);
    }
    
    public static Generator getGenerator(IHasScope scopeUnit, ICompilationUnit compilationUnit, IExpression expression) {
        return getGenerator(scopeUnit, compilationUnit, expression, null);
    }
    
    public static Generator getGenerator(IHasScope scopeUnit, IExpression expression, IType expectedType) {
        return getGenerator(scopeUnit, scopeUnit.getCompilationUnit(), expression, expectedType);
    }
    
    public static Generator getGenerator(IHasScope scopeUnit, ICompilationUnit compilationUnit, IExpression expression, IType expectedType) {
        if (expression instanceof ConstantLiteral constantLiteral)
            return new ConstantLiteralGenerator(scopeUnit, constantLiteral);
        else if (expression instanceof StringLiteral stringLiteral)
            return new StringLiteralGenerator(scopeUnit, stringLiteral);
        else if (expression instanceof Identifier identifier)
            return new IdentifierGenerator(scopeUnit, compilationUnit, identifier, expectedType);
        else if (expression instanceof MinusExpression minusExpression)
            return new MinusExpressionGenerator(scopeUnit, minusExpression);
        else if (expression instanceof FunctionCall functionCall)
            return new FunctionCallGenerator(scopeUnit, functionCall, expectedType);
        else if (expression instanceof InfixOpExpression infixOpExpression)
            return new InfixOpExpressionGenerator(scopeUnit, infixOpExpression);
        else if (expression instanceof Dereference dereference)
            return new DereferenceGenerator(scopeUnit, dereference);
        else if (expression instanceof ArrayAccess arrayAccess)
            return new ArrayAccessGenerator(scopeUnit, arrayAccess);
        else if (expression instanceof ParenthesedExpression parentheseExpression)
            return new ParenthesedExpressionGenerator(scopeUnit, parentheseExpression);
        else if (expression instanceof FieldAccess fieldAccess)
            return new FieldAccessGenerator(scopeUnit, fieldAccess);
        else if (expression instanceof SetExpression setExpression)
            return new SetExpressionGenerator(scopeUnit, setExpression);
        throw new CompilerException(expression, "Unhandled expression type " + expression.getClass().getSimpleName());
    }

}
