package ch.bbw.m226.nils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class StoryTeller {
    private final StoryView view;
    private final Story story;
    private Set<String> states;
    private Story.Room currentRoom;

    public StoryTeller(StoryView view, Story story) {
        this.view = view;
        this.story = story;
        this.states = new HashSet<>();
    }

    public void init() {
        this.goToRoom(story.startRoom());
    }

    public void step() {
        var instruction = view.readInstruction();

        if (instruction == Instruction.EXIT_GAME) {
            view.writeLine("Exiting game.");
            throw new ExitException();
        }

        if (instruction.verb().equals("?save")) {
            this.save(instruction.noun());
            return;
        }

        if (instruction.verb().equals("?load")) {
            this.load(instruction.noun());
            return;
        }

        var optionalAction = this.findAction(instruction, this.currentRoom);
        optionalAction.ifPresentOrElse(this::executeAction, () -> view.writeLine("This action is not supported."));
    }

    private void load(String pathNull) {
        var path = Optional.ofNullable(pathNull).orElse("savefile.json");

        try {
            var bufRead = Files.newBufferedReader(Paths.get(path));
            var saveData = new ObjectMapper().readValue(bufRead, SaveState.class);

            this.states = saveData.states();

            this.goToRoom(saveData.currentRoom());

        } catch (IOException e) {
            System.err.println("Failed to read file, no data loaded: " + e.getMessage());
        }
    }

    private void save(String pathNull) {
        var path = Optional.ofNullable(pathNull).orElse("savefile.json");

        var saveData = new SaveState(
                this.states,
                this.story.rooms().entrySet().stream()
                        .filter(entry -> entry.getValue().equals(this.currentRoom))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow()
        );

        try {
            var bufWrite = Files.newBufferedWriter(Paths.get(path));
            new ObjectMapper().writeValue(bufWrite, saveData);

            this.view.writeLine("Saved game under path `" + path + "`");

        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
        }
    }

    private void goToRoom(String name) {
        if (name.equals("_exit")) {
            throw new ExitException();
        }

        this.currentRoom = this.story.rooms().get(name);

        if (this.currentRoom == null) {
            throw new RuntimeException("Invalid YAML file! Room '" + name + "' does not exist!");
        }

        this.enterRoom(this.currentRoom);
    }

    private void enterRoom(Story.Room room) {
        view.writeLine(room.message());

        String possibleActions = String.join(", ", room.verbs().keySet());
        view.writeLine("Possible actions: " + possibleActions);
    }

    private Optional<Story.Action> findAction(Instruction instruction, Story.Room room) {
        // first, try to get the verb directly
        var optionalVerb = Optional.ofNullable(room.verbs().get(instruction.verb()));

        // if it's not found, try to search for a synonym
        var optionalVerbSynonym = optionalVerb.or(() ->
                story.synonymFor(instruction.verb())
                        .flatMap(verb -> Optional.ofNullable(room.verbs().get(verb)))
        );

        return optionalVerbSynonym.flatMap(nouns -> {
            // we've found a verb, now look for the noun
            var actions = Optional.ofNullable(nouns.get(instruction.noun()))
                    .orElseGet(ArrayList::new);


            return actions.stream()
                    .filter(action ->
                            action.ifState() == null
                                    || this.states.contains(action.ifState())
                    )
                    .findFirst();
        });
    }

    private void executeAction(Story.Action action) {
        view.writeLine(action.message());

        if (action.setState() != null) {
            this.states.add(action.setState());
        }

        if (action.room() != null) {
            this.goToRoom(action.room());
        }
    }
}