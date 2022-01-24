package ch.bbw.m226.nils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class StoryTellerTest {

    private static final Story EMPTY_STORY;

    static {
        var emptyMap = new HashMap<String, Story.Room>();
        emptyMap.put("start", new Story.Room("", Map.of("testverb",
                Map.of("testnoun",
                        List.of(new Story.Action("test", null, null, "badstate"))
                )
        )));

        EMPTY_STORY = new Story("start", emptyMap,
                Map.of("default", new Story.Verb(
                        new Story.Errors("invalid verb", "invalid object", "invalid state"), new ArrayList<>()
                )),
                new ArrayList<>()
        );
    }

    @Test
    void printsInvalidAction() {
        var output = new ArrayList<String>();
        var view = new MockView(output);

        var teller = new StoryTeller(view, EMPTY_STORY);
        teller.init();

        view.queueInput(new Instruction("invalid", "invalid"));
        view.queueInput(new Instruction("testverb", "invalid"));
        view.queueInput(new Instruction("testverb", "testnoun"));

        teller.step();
        teller.step();
        teller.step();

        assertEquals("invalid verb", output.get(2));
        assertEquals("invalid object", output.get(3));
        assertEquals("invalid state", output.get(4));
    }


    static class MockView implements StoryView {
        private final List<String> lines;
        private final Queue<Instruction> next;

        public MockView(List<String> lines) {
            this.lines = lines;
            this.next = new ArrayDeque<>();
        }

        @Override
        public void writeLine(String message, Colors.Color _color) {
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
