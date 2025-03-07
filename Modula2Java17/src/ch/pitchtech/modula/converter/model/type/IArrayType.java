package ch.pitchtech.modula.converter.model.type;


/**
 * Common interface for {@link ArrayType} and {@link OpenArrayType}
 */
public interface IArrayType {
    
    public IType getElementType();

}
