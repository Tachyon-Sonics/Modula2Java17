package ch.pitchtech.modula.converter.generator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.v4.runtime.CommonTokenStream;

import ch.pitchtech.modula.converter.compiler.CompilerOptions;
import ch.pitchtech.modula.converter.model.expression.IExpression;
import ch.pitchtech.modula.converter.model.scope.IHasScope;
import ch.pitchtech.modula.converter.model.scope.IScope;
import ch.pitchtech.modula.converter.model.scope.TypeResolver;
import ch.pitchtech.modula.converter.model.source.SourceLocation;
import ch.pitchtech.modula.converter.model.type.IType;
import ch.pitchtech.modula.converter.model.type.ProcedureType;
import ch.pitchtech.modula.converter.utils.CommentConverter;
import ch.pitchtech.modula.converter.utils.CommentExtractor;

public class ResultContext {

    private final CompilerOptions compilerOptions;
    private final boolean main;
    private final StringBuilder builder = new StringBuilder();
    private final Stack<IScope> scopeStack = new Stack<>();
    private Set<String> requiredJavaImports = new TreeSet<>();
    private Set<String> requiredModuleInstances = new TreeSet<>();
    private Set<String> allocatedNames = new HashSet<>();
    private int indent;
    private Map<IExpression, IType> requestedTypes = new HashMap<>();

    // Comment management
    private TreeMap<Integer, List<CommentExtractor.Comment>> commentsByLine;
    private Set<CommentExtractor.Comment> emittedComments = new HashSet<>();
    

    public ResultContext(CompilerOptions compilerOptions) {
        this(compilerOptions, true, null);
    }

    public ResultContext(CompilerOptions compilerOptions, CommonTokenStream tokenStream) {
        this(compilerOptions, true, tokenStream);
    }

    public ResultContext(CompilerOptions compilerOptions, boolean main) {
        this(compilerOptions, main, null);
    }

    public ResultContext(CompilerOptions compilerOptions, boolean main, CommonTokenStream tokenStream) {
        this.compilerOptions = compilerOptions;
        this.main = main;
        if (tokenStream != null) {
            initializeComments(tokenStream);
        }
    }

    /**
     * Initialize the comment map from the token stream.
     * Comments are indexed by their starting line number for efficient lookup.
     */
    private void initializeComments(CommonTokenStream tokenStream) {
        List<CommentExtractor.Comment> allComments = CommentExtractor.extractComments(tokenStream);
        commentsByLine = new TreeMap<>();

        for (CommentExtractor.Comment comment : allComments) {
            commentsByLine
                .computeIfAbsent(comment.getLine(), k -> new java.util.ArrayList<>())
                .add(comment);
        }
    }
    
    public CompilerOptions getCompilerOptions() {
        return compilerOptions;
    }

    public void write(String text) {
        builder.append(text);
    }
    
    public void writeIndent() {
        for (int k = 0; k < indent; k++) {
            write("    ");
        }
    }
    
    /**
     * Write the "\n" new line
     */
    public void writeLn() {
        builder.append("\n");
    }
    
    /**
     * Write current indentation spaces, then the given text, then a new line
     */
    public void writeLine(String text) {
        writeIndent();
        write(text);
        writeLn();
    }
    
    public void incIndent() {
        indent++;
    }
    
    public void decIndent() {
        indent--;
    }
    
    public void pushScope(IScope scope) {
        scopeStack.push(scope);
    }
    
    public void popScope() {
        scopeStack.pop();
    }
    
    public IScope getScope() { // TODO check if not redundant. Most INode have a scope now
        return scopeStack.peek().full();
    }
    
    /**
     * Optimisation when "MIN(enumType)" is used in array bounds: we explicitely request INTEGER type instead
     */
    public IType popRequestedReturnType(IExpression expr) {
        return requestedTypes.remove(expr);
    }

    /**
     * Note: values are not transferred by {@link #subContext()}
     * @see #popRequestedReturnType(IExpression)
     */
    public void pushRequestedReturnType(IExpression expr, IType requestedReturnType) {
        if (main)
            throw new UnsupportedOperationException("Can only be used on a subContext");
        requestedTypes.put(expr, requestedReturnType);
    }

    /**
     * Resolve a type for Java generation.
     * <p>
     * If this is a literal type, resolve up to the built-in type, because Java does not support type aliases.
     * Else, return the type unmodified.
     */
    public IType resolveType(IType type) {
        return TypeResolver.resolveType(getScope(), type);
    }
    
    /**
     * Resolve the given type, replacing "PROC" by an appropriate {@link ProcedureType}
     */
    public IType resolveProcedureType(IType type, IHasScope scopeUnit) {
        return TypeResolver.resolveProcedureType(type, scopeUnit);
    }
    
    public IType resolveType(IExpression expression) {
        return resolveType(expression.getType(getScope()));
    }
    
    public IType resolveProcedureType(IExpression expression, IHasScope scopeUnit) {
        return TypeResolver.resolveProcedureType(expression, scopeUnit);
    }

    public IType resolveType(IExpression expression, IType forType) {
        return resolveType(expression.getType(getScope(), forType));
    }
    
    public ResultContext subContext() {
        ResultContext result = new ResultContext(this.compilerOptions, false, null);
        result.indent = this.indent;
        result.requiredJavaImports = this.requiredJavaImports;
        result.requiredModuleInstances = this.requiredModuleInstances;
        result.allocatedNames = this.allocatedNames;
        result.commentsByLine = this.commentsByLine;
        result.emittedComments = this.emittedComments;
        result.pushScope(getScope());
        return result;
    }
    
    public void write(ResultContext subContext) {
        write(subContext.builder.toString());
    }
    
    public void ensureJavaImport(Class<?> clazz) {
        requiredJavaImports.add(clazz.getName());
    }
    
    public void ensureJavaImport(String className) {
        requiredJavaImports.add(className);
    }
    
    public Set<String> getRequiredJavaImports() {
        return requiredJavaImports;
    }
    
    public void ensureModuleInstance(String name) {
        requiredModuleInstances.add(name);
    }
    
    public Set<String> getRequiredModuleInstances() {
        return requiredModuleInstances;
    }

    public String allocateName(String name, boolean alwaysSuffix) {
        int index = 1;
        String result = name;
        if (alwaysSuffix)
            result += "1";
        while (allocatedNames.contains(result)) {
            index++;
            result = name + index;
        }
        allocatedNames.add(result);
        return result;
    }
    
    public void freeName(String name) {
        if (!allocatedNames.contains(name))
            throw new IllegalStateException("Freeing unused name: " + name);
        allocatedNames.remove(name);
    }

    /**
     * Write comments that appear immediately before the given source location.
     * This method finds comments on the lines just before the element and emits them
     * with proper indentation.
     *
     * @param location the source location of the code element
     */
    public void writeCommentsFor(SourceLocation location) {
        if (commentsByLine == null || location == null) {
            return;
        }

        int targetLine = location.startLine();

        // Find comments that appear just before this element (within 10 lines)
        // This handles both same-line comments and comments on preceding lines,
        // including multi-line nested comments that may span several lines
        for (int line = Math.max(1, targetLine - 10); line <= targetLine; line++) {
            List<CommentExtractor.Comment> comments = commentsByLine.get(line);
            if (comments != null) {
                for (CommentExtractor.Comment comment : comments) {
                    // Only emit each comment instance once
                    if (!emittedComments.contains(comment)) {
                        String javaComment = CommentConverter.convertToJavaComment(comment.getText());
                        writeMultiLineComment(javaComment);
                        emittedComments.add(comment);
                    }
                }
            }
        }
    }

    /**
     * Write a potentially multi-line comment with proper indentation on each line.
     *
     * @param comment the comment text (may contain newlines)
     */
    private void writeMultiLineComment(String comment) {
        String[] lines = comment.split("\n", -1);
        for (int i = 0; i < lines.length; i++) {
            if (i == lines.length - 1 && lines[i].isEmpty()) {
                // Don't write a trailing empty line
                break;
            }
            writeLine(lines[i]);
        }
    }

    @Override
    public String toString() {
        return builder.toString();
    }

}
