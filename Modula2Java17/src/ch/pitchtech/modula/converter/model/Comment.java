package ch.pitchtech.modula.converter.model;

/**
 * Represents a comment extracted from the source code.
 */
public record Comment(String text, int line, int column) {

}