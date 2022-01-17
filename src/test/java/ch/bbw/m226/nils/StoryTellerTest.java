package ch.bbw.m226.nils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StoryTellerTest {

    private static final Story EMPTY_STORY;

    static {
        var emptyMap = new HashMap<String, Story.Room>();
        emptyMap.put("start", new Story.Room("", new HashMap<>()));

        EMPTY_STORY = new Story("start", emptyMap, new HashMap<>(), new ArrayList<>());
    }

    @Test
    void printsInvalidAction() {
        var output = new ArrayList<String>();
        var view = new MockView(output);

        var teller = new StoryTeller(view, EMPTY_STORY);
        teller.init();

        view.queueInput(new Instruction("invalid", "action"));

        teller.step();

        assertEquals("This action is not supported.", output.get(2));
    }


    static class MockView implements StoryView {
        private final List<String> lines;
        private final Queue<Instruction> next;

        public MockView(List<String> lines) {
            this.lines = lines;
            this.next = new ArrayDeque<>();
        }

        @Override
        public void writeLine(String message) {
            this.lines.add(message);
        }

        public void queueInput(Instruction next) {
            this.next.add(next);
        }

        @Override
        public Instruction readInstruction() {
            return this.next.poll();
        }
    }
}
