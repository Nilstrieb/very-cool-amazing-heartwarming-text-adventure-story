package ch.bbw.m226.nils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public class Main {
    private static final String DEFAULT_PATH = "adventure.yml";

    public static void main(String[] args) {
        try {
            Story story = new StoryReader().read(DEFAULT_PATH);

            StoryView storyView = new ConsoleStoryView();

            StoryTeller storyTeller = new StoryTeller(storyView, story);

            storyTeller.init();

            while (true) {
                storyTeller.step();
            }

        } catch (JsonProcessingException e) {
            System.err.println("Invalid yaml file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not find file `" + DEFAULT_PATH + "`: " + e.getMessage());
        }
    }
}
