package ch.bbw.m226.nils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public class Main {
    private static final String DEFAULT_PATH = "adventure.yml";
    private static final int WIDTH = 50;
    private static final int INNER_WIDTH = WIDTH - 2;

    public static void main(String[] args) {
        var filePath = DEFAULT_PATH;

        if (args.length > 1) {
            filePath = args[0];
        } else {
            var env = System.getenv("TEXT_ADVENTURE_SOURCE");
            if (env != null) {
                filePath = env;
            }
        }

        try {
            Story story = new StoryReader().read(filePath);

            StoryView storyView = new ConsoleStoryView();

            StoryTeller storyTeller = new StoryTeller(storyView, story);

            storyTeller.init();

            // this is *not* a great solution, but it works.
            try {
                while (true) {
                    storyTeller.step();
                }
            } catch (ExitException ignored) {
            }

            writeCredits(story);

        } catch (JsonProcessingException e) {
            System.err.println("Invalid yaml file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not find file `" + DEFAULT_PATH + "`: " + e.getMessage());
        }
    }


    private static void writeCredits(Story story) throws IOException {
        var dashes = "─".repeat(INNER_WIDTH);
        var empty = "│" + " ".repeat(INNER_WIDTH) + "│";

        System.out.println("╭" + dashes + "╮");
        System.out.println(empty);
        System.out.println(center("AUTHORS"));
        System.out.println(empty);

        for (var author : story.authors()) {
            System.out.println(center(author.name()));
            System.out.println(center(author.function()));
            System.out.println(empty);
        }
        System.out.println("╰" + dashes + "╯");
    }

    private static String center(String msg) {
        var len = msg.length();

        if (len > INNER_WIDTH) {
            throw new IllegalArgumentException("string too long");
        }

        var pad = ((double) (INNER_WIDTH - len)) / 2;

        return "│" + " ".repeat((int) pad) + msg + " ".repeat((int) Math.round(pad)) + "│";
    }
}
