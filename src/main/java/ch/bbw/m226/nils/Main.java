package ch.bbw.m226.nils;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    private static final Path DEFAULT_PATH = Path.of("adventure.yml");

    public static void main(String[] args) {
        try {
            Story story = new StoryReader().read(DEFAULT_PATH);
            System.out.println(story);
        } catch (JsonProcessingException e) {
            System.err.println("Invalid yaml file: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Could not find file `" + DEFAULT_PATH + "`: " + e.getMessage());
        }
    }
}
