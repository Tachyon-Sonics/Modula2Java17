package ch.pitchtech.modula.converter.model.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceElement;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;

// MySetType{value1, value2}
public class SetExpression extends SourceElement implements IExpression {
    
    public static record SetRange(IExpression lower, IExpression upper) {
        
    }
    
    private final IType setType;
    private final List<IExpression> setValues;
    private final List<SetRange> setRanges = new ArrayList<>();

    
    public SetExpression(SourceLocation sLoc, IType setType, List<IExpression> setValues) {
        super(sLoc);
        this.setType = setType;
        this.setValues = setValues;
        attach(setValues, NodeAttachType.READ_ACCESS);
    }

    public IType getSetType() {
        return setType;
    }

    public List<IExpression> getSetValues() {
        return Collections.unmodifiableList(setValues);
    }
    
    public List<SetRange> getSetRanges() {
        return Collections.unmodifiableList(setRanges);
    }
    
    public void addSetRanges(List<SetRange> setRanges) {
        this.setRanges.addAll(setRanges);
        for (SetRange setRange : setRanges) {
            attach(setRange.lower(), NodeAttachType.READ_ACCESS);
            attach(setRange.upper(), NodeAttachType.READ_ACCESS);
        }
    }

    @Override
    public boolean isConstant(IScope scope) {
        for (IExpression value : setValues)
            if (!value.isConstant(scope))
                return false;
        for (SetRange range : setRanges) {
            if (!range.lower().isConstant(scope) || !range.upper().isConstant(scope))
                return false;
        }
        return true;
    }

    @Override
    public IType getType(IScope scope, IType forType) {
        return setType;
    }

    @Override
    public String toString() {
        return "SetExpression [setType=" + setType + ", setValues=" + setValues + "]";
    }

}
