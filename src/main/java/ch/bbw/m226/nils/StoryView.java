package ch.bbw.m226.nils;

public interface StoryView {
    void writeLine(String message, Colors.Color color);

    default void writeLine(String message) {
        this.writeLine(message, Colors.Color.NONE);
    }

    Instruction readInstruction();
}
