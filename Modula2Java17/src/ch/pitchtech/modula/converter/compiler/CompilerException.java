package ch.pitchtech.modula.converter.compiler;

import ch.pitchtech.modula.converter.utils.StringUtils;

/**
 * Models a an unexpected internal error while compiling. Usually this is du to a bug
 * in the compiler itself, but can also happen on incorrect code that is
 * not properly detected and reported as a {@link CompilationException}.
 */
public class CompilerException extends RuntimeException {

    private final Object element;

    
    /**
     * Create an internal, unexpected compiler error
     */
    public CompilerException(Object element, String message, Object... args) {
        super(StringUtils.format(message, args) + CompilationException.getLocationDetails(element));
        this.element = element;
    }

    public Object getElement() {
        return element;
    }
    

}
