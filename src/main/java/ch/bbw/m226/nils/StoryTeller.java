package ch.bbw.m226.nils;

import java.util.*;

public class StoryTeller {
    private final StoryView view;
    private final Story story;
    private final Set<String> states;

    private Story.Room currentRoom;

    public StoryTeller(StoryView view, Story story) {
        this.view = view;
        this.story = story;
        this.states = new HashSet<>();
    }

    public void init() {
        this.goToRoom("start");
    }

    public void step() {
        var instruction = view.readInstruction();
        var optionalAction = this.findAction(instruction, this.currentRoom);
        optionalAction.ifPresentOrElse(this::executeAction, () -> view.writeLine("This action is not supported."));
    }

    private void goToRoom(String name) {
        this.currentRoom = this.story.rooms().get(name);

        if (this.currentRoom == null) {
            throw new RuntimeException("Invalid YAML file! Room '" + name + "' does not exist!");
        }

        this.enterRoom(this.currentRoom);
    }

    private void enterRoom(Story.Room room) {
        view.writeLine(room.message());
    }

    private Optional<Story.Action> findAction(Instruction instruction, Story.Room room) {
        var optionalVerb = room.verbs()
                .entrySet()
                .stream()
                .filter(possibleVerb -> possibleVerb.getKey().equals(instruction.verb()))
                .findAny(); // imagine rusts `?` here

        return optionalVerb.flatMap(verb -> {
            var nouns = verb.getValue().entrySet();

            var actions = nouns.stream()
                    .filter(noun -> noun.getKey().equals(instruction.noun()))
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElseGet(ArrayList::new);


            return actions.stream()
                    .filter(action ->
                            action.ifState() == null || this.states.contains(action.ifState())
                    )
                    .findFirst();
        });
    }

    private void executeAction(Story.Action action) {
        view.writeLine(action.message());

        if (action.setState() != null) {
            this.states.add(action.setState());
        }

        if (action.goRoom() != null) {
            this.goToRoom(action.goRoom());
        }
    }
}