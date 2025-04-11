package ch.pitchtech.modula.converter.compiler;


public class CompilerOptions {
    
    private boolean euclideanDivMod = false;
    private boolean convertArrayOfCharToString = true;
    private boolean useRecordHelper = false;
    private boolean markSimplifiedWith = false;
    private boolean optimizeUnwrittenByRefArguments = true;
    private boolean inlineProcedureAsExpression = false;
    private boolean alwaysOverrideStubs = false;
    
    private String targetPackageMain; // Target package for main Java files
    private String targetPackageLib; // Target package for library Java files
    
    
    /**
     * Whether to use Euclidean division for "DIV" and "MOD" on INTEGER.
     * <p>
     * See https://gcc.gnu.org/onlinedocs/gm2/Dialect.html
     * <p>
     * Note that "/" and "REM" always use truncated division (like Java)
     */
    public boolean isEuclideanDivMod() {
        return euclideanDivMod;
    }
    
    public void setEuclideanDivMod(boolean euclideanDivMod) {
        this.euclideanDivMod = euclideanDivMod;
    }
    
    /**
     * Whether ARRAY OF CHAR must be converted to Java {@link String}
     */
    public boolean isConvertArrayOfCharToString() {
        return convertArrayOfCharToString;
    }
    
    public void setConvertArrayOfCharToString(boolean convertArrayOfCharToString) {
        this.convertArrayOfCharToString = convertArrayOfCharToString;
    }
    
    public boolean isUseRecordHelper() {
        return useRecordHelper;
    }
    
    public void setUseRecordHelper(boolean useRecordHelper) {
        this.useRecordHelper = useRecordHelper;
    }
    
    public boolean isMarkSimplifiedWith() {
        return markSimplifiedWith;
    }
    
    public void setMarkSimplifiedWith(boolean markSimplifiedWith) {
        this.markSimplifiedWith = markSimplifiedWith;
    }
    
    /**
     * Normally, pass-by-value (non-VAR) arguments are copied on method entry if they correspond to a Java object
     * in order to prevent undesired pass-by-ref semantics.
     * <p>
     * When <tt>true</tt>, this copy is dropped if the PROCEDURE never assigns a new value to such an argument.
     * <p>
     * In practice, this might have undesired behavior on multi-threaded applications.
     */
    public boolean isOptimizeUnwrittenByRefArguments() {
        return optimizeUnwrittenByRefArguments;
    }
    
    public void setOptimizeUnwrittenByRefArguments(boolean optimizeUnwrittenByRefArguments) {
        this.optimizeUnwrittenByRefArguments = optimizeUnwrittenByRefArguments;
    }

    public boolean isInlineProcedureAsExpression() {
        return inlineProcedureAsExpression;
    }

    public void setInlineProcedureAsExpression(boolean inlineProcedureAsExpression) {
        this.inlineProcedureAsExpression = inlineProcedureAsExpression;
    }
    
    public boolean isAlwaysOverrideStubs() {
        return alwaysOverrideStubs;
    }
    
    public void setAlwaysOverrideStubs(boolean alwaysOverrideStubs) {
        this.alwaysOverrideStubs = alwaysOverrideStubs;
    }

    public String getTargetPackageMain() {
        return targetPackageMain;
    }

    public void setTargetPackageMain(String targetPackageMain) {
        this.targetPackageMain = targetPackageMain;
    }
    
    public String getTargetPackageLib() {
        return targetPackageLib;
    }

    public void setTargetPackageLib(String targetPackageLib) {
        this.targetPackageLib = targetPackageLib;
    }

}
