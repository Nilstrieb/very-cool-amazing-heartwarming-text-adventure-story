package ch.bbw.m226.nils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StoryTeller {
    private final StoryView view;
    private final Story story;
    private final Set<String> states;

    public StoryTeller(StoryView view, Story story) {
        this.view = view;
        this.story = story;
        this.states = new HashSet<>();
    }

    public void start() {
        this.enterRoom(story.rooms().get("outside"));
    }

    private void enterRoom(Story.Room room) {
        view.writeLine(room.message());

        var instruction = view.readInstruction();

        var optionalAction = this.findAction(instruction, room);

        optionalAction.ifPresentOrElse(this::executeAction, () -> {
            view.writeLine("This action is not supported.");
        });
    }

    private Optional<Story.Action> findAction(Instruction instruction, Story.Room room) {
        var optionalVerb = room.verbs()
                .entrySet()
                .stream()
                .filter(possibleVerb -> possibleVerb.getKey().equals(instruction.verb()))
                .findAny(); // imagine rusts `?` here

        return optionalVerb.flatMap(verb -> {
            var nouns = verb.getValue().entrySet();

            return nouns.stream()
                    .filter(noun -> noun.getKey().equals(instruction.noun()))
                    .flatMap(noun -> noun.getValue().stream())
                    .filter(possibleAction ->
                            possibleAction.ifState() == null || this.states.contains(possibleAction.ifState())
                    )
                    .findFirst();
        });
    }

    private void executeAction(Story.Action action) {}
}