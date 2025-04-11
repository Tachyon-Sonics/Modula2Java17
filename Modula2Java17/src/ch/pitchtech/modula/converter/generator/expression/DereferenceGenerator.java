package ch.pitchtech.modula.converter.generator.expression;

import ch.pitchtech.modula.converter.compiler.CompilationException;
import ch.pitchtech.modula.converter.compiler.CompilerException;
import ch.pitchtech.modula.converter.generator.Generator;
import ch.pitchtech.modula.converter.generator.ResultContext;
import ch.pitchtech.modula.converter.generator.statement.AssignmentGenerator;
import ch.pitchtech.modula.converter.generator.type.TypeHelper;
import ch.pitchtech.modula.converter.model.expression.Dereference;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.type.IArrayType;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.LiteralType;
import ch.pitchtech.modula.converter.model.type.PointerType;
import ch.pitchtech.modula.converter.model.type.RecordType;


public class DereferenceGenerator extends Generator implements IAssignGenerator {
    
    private final Dereference dereference;

    
    public DereferenceGenerator(IHasScope scopeUnit, Dereference dereference) {
        super(scopeUnit, dereference);
        this.dereference = dereference;
    }

    @Override
    public void generate(ResultContext result) {
        IExpression pointer = dereference.getPointer();
        IType type = result.resolveType(pointer);
        if (!(type instanceof PointerType pointerType))
            throw new CompilationException(dereference, "Not pointer type: " + pointer + "; type: " + type);
        IType targetType = result.resolveType(pointerType.getTargetType());
        if (targetType instanceof RecordType || targetType instanceof IArrayType) {
            // RecordType and ArrayType are already Java references
            Expressions.getGenerator(scopeUnit, dereference.getPointer()).generate(result);
        } else if (targetType instanceof LiteralType literalType && literalType.isBuiltIn()) {
            // This was wrapped into an IRef
            Expressions.getGenerator(scopeUnit, dereference.getPointer()).generate(result);
            result.write(".get()");
        } else {
            throw new CompilerException(dereference, "Unhandled target type: {0}", targetType);
        }
    }
    
    private boolean isRefAssignment(ResultContext result) {
        IExpression pointer = dereference.getPointer();
        IType targetType = result.resolveType(pointer);
        if (!(targetType instanceof PointerType pointerType))
            throw new CompilationException(dereference, "Not pointer type: " + pointer + "; type: " + targetType);
        IType derefType = result.resolveType(pointerType.getTargetType());
        return TypeHelper.isByValueType(derefType, result.getScope(), result.getCompilerOptions());
    }

    @Override
    public boolean generateAssignement(ResultContext result, IExpression value) {
        if (isRefAssignment(result)) {
            // This was wrapped into an IRef
            ResultContext assignedValue = result.subContext();
            IType valueType = result.resolveType(value);
            IExpression pointer = dereference.getPointer();
            PointerType pointerType = (PointerType) result.resolveType(pointer);
            IType derefType = result.resolveType(pointerType.getTargetType());
            AssignmentGenerator.writeValueWithProperCast(assignedValue, scopeUnit, derefType, true, value, valueType, false, false);
            generateRefAssignment(result, assignedValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean generateAssignement(ResultContext result, ResultContext assignedValue) {
        if (isRefAssignment(result)) {
            // This was wrapped into an IRef
            generateRefAssignment(result, assignedValue);
            return true;
        }
        return false;
    }

    private void generateRefAssignment(ResultContext result, ResultContext assignedValue) {
        Expressions.getGenerator(scopeUnit, dereference.getPointer()).generate(result);
        result.write(".set(");
        result.write(assignedValue);
        result.write(")");
    }

}
