package ch.bbw.m226.nils;

import java.util.Scanner;

public class ConsoleStoryView implements StoryView {
    private Scanner stdin = new Scanner(System.in);

    @Override
    public void writeLine(String message) {
        System.out.println(message);
    }

    @Override
    public String readLine() {
        return stdin.nextLine().trim();
    }
}
