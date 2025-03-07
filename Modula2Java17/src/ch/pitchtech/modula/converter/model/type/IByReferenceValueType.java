package ch.pitchtech.modula.converter.model.type;

/**
 * IType that is a value type in Modula-2, but a reference type in Java:
 * The values must be compared using {@link #equals(Object)} in Java, and assignement must use a method such as copyFrom()
 */
public interface IByReferenceValueType extends IType {

}
