package ch.bbw.m226.nils;

public record Instruction(String verb, String noun) {
    public static final Instruction EXIT_GAME = new Instruction(null, null);
}
