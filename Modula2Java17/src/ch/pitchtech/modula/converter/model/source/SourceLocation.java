package ch.pitchtech.modula.converter.model.source;


// All inclusive- Line from 1, column from 0
public record SourceLocation(int startLine, int startColumn, int stopLine, int stopColumn) {

}
