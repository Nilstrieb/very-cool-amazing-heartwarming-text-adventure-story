package ch.bbw.m226.nils;

import java.util.HashSet;
import java.util.Set;

public class StoryTeller {
    private StoryView view;
    private Story story;
    private Set<String> states;

    public StoryTeller(StoryView view, Story story) {
        this.view = view;
        this.story = story;
        this.states = new HashSet<>();
    }

    public void start() {

    }
}
