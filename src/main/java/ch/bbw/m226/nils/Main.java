package ch.bbw.m226.nils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public class Main {
    private static final String DEFAULT_PATH = "adventure.yml";

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

            System.out.println("""
                    Written and made by nils
                    SQL-Room by corsin
                    """);

        } catch (JsonProcessingException e) {
            System.err.println("Invalid yaml file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not find file `" + DEFAULT_PATH + "`: " + e.getMessage());
        }
    }
}
