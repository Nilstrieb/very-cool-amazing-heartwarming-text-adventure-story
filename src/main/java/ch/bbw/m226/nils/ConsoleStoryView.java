package ch.bbw.m226.nils;

import java.util.Scanner;

public class ConsoleStoryView implements StoryView {
    private final Scanner stdin = new Scanner(System.in);

    @Override
    public void writeLine(String message) {
        System.out.println(message);
    }

    @Override
    public Instruction readInstruction() {
        Instruction instruction;

        while (true) {

            var input = stdin.nextLine().trim();
            var split = input.split("\\s+");

            if (split[0].equals("?quit") || split[0].equals("?exit")) {
                return Instruction.EXIT_GAME;
            }

            if (split[0].equals("?save")) {
                String path = null;

                if (split.length > 1) {
                    path = split[1];
                }

                return new Instruction("?save", path);
            }

            if (split[0].equals("?load")) {
                String path = null;

                if (split.length > 1) {
                    path = split[1];
                }

                return new Instruction("?load", path);
            }

            if (split.length < 2) {
                this.writeLine("Invalid instruction. Instruction must contain a verb and a noun.");
                continue;
            }

            instruction = new Instruction(split[0], split[1]);
            break;
        }

        return instruction;
    }
}
