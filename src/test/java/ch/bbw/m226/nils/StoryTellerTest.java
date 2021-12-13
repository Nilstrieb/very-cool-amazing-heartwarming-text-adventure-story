package ch.bbw.m226.nils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryTellerTest {

    private static final Story EMPTY_STORY;

    static {
        var emptyMap = new HashMap<String, Story.Room>();
        emptyMap.put("start", new Story.Room("", new HashMap<>()));

        EMPTY_STORY = new Story(emptyMap);
    }

    @Test
    void printsInvalidAction() {
        var output = new ArrayList<String>();
        var view = new MockView(output);

        var teller = new StoryTeller(view, EMPTY_STORY);

    }


    static class MockView implements StoryView {
        private List<String> lines;
        private Instruction next;

        public MockView(List<String> lines) {
            this.lines = lines;
        }

        @Override
        public void writeLine(String message) {
            this.lines.add(message);
        }

        public void setNext(Instruction next) {
            this.next = next;
        }

        @Override
        public Instruction readInstruction() {
            return next;
        }
    }
}
