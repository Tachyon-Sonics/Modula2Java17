package ch.pitchtech.modula.converter.model.statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.pitchtech.modula.converter.model.expression.FunctionCall;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.source.NodeAttachType;
import ch.pitchtech.modula.converter.model.source.SourceLocation;

public class ProcedureCall extends StaticCall implements IStatement, IMethodCall {

    private final String procedureName;
    private final List<IExpression> arguments = new ArrayList<>();
            
    
    /**
     * @param moduleName optional module name (if qualified access). <tt>null</tt> if this is an unqualified access
     */
    public ProcedureCall(SourceLocation sLoc, IHasScope scopeUnit, String moduleName, String procedureName) {
        super(sLoc, scopeUnit, moduleName);
        this.procedureName = procedureName;
    }
    
    public String getModuleName() {
        return moduleName;
    }

    public String getProcedureName() {
        return procedureName;
    }
    
    @Override
    public String getName() {
        return procedureName;
    }
    
    /**
     * Get the definition module if this call is qualified, or <tt>null</tt> else
     */
    public IHasScope getQualifiedScope() {
        if (moduleName != null)
            return this.scopeUnit;
        return null;
    }
    
    @Override
    public List<IExpression> getArguments() {
        return Collections.unmodifiableList(arguments);
    }
    
    public void addArguments(List<IExpression> arguments) {
        this.arguments.addAll(arguments);
        attach(arguments, NodeAttachType.READ_ACCESS);
    }
    
    public FunctionCall asFunction() {
        FunctionCall result = new FunctionCall(getSourceLocation(), scopeUnit, moduleName, procedureName);
        result.addArguments(arguments);
        return result;
    }

    @Override
    public String toString() {
        return "ProcedureCall [moduleName=" + moduleName + ", procedureName=" + procedureName + ", arguments=" + arguments + "]";
    }

}
