package ch.bbw.m226.nils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Optional.ofNullable;

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

        try {
            var action = this.findAction(instruction, this.currentRoom);
            this.executeAction(action);
        } catch (ActionNotFoundException notFoundException) {
            var message = this.getError(instruction, notFoundException.kind());
            this.view.writeLine(message);
        }
    }

    private void load(String pathNull) {
        var path = ofNullable(pathNull).orElse("savefile.json");

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
        var path = ofNullable(pathNull).orElse("savefile.json");

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

    private Story.Action findAction(Instruction instruction, Story.Room room) throws ActionNotFoundException {
        // first, try to get the verb directly
        var optionalVerb = ofNullable(room.verbs().get(instruction.verb()));

        // if it's not found, try to search for a synonym
        var optionalVerbSynonym = optionalVerb.or(() ->
                story.synonymFor(instruction.verb())
                        .flatMap(verb -> ofNullable(room.verbs().get(verb)))
        );

        var nouns = optionalVerbSynonym.orElseThrow(() ->
                new ActionNotFoundException(ActionNotFoundException.NotFoundKind.VERB)
        );

        // we've found a verb, now look for the noun
        var optionalActions = ofNullable(nouns.get(instruction.noun()));

        var actions = optionalActions.orElseThrow(() ->
                new ActionNotFoundException(ActionNotFoundException.NotFoundKind.OBJECT)
        );


        return actions.stream()
                .filter(action ->
                        action.ifState() == null
                                || this.states.contains(action.ifState())
                )
                .findFirst()
                .orElseThrow(() -> new ActionNotFoundException(ActionNotFoundException.NotFoundKind.STATE));

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

    private String getError(Instruction instruction, ActionNotFoundException.NotFoundKind kind) {
        var defaultVerb = this.story.verbs().get("default");

        var verb = ofNullable(this.story.verbs().get(instruction.noun()))
                .or(() -> this.story.synonymFor(instruction.verb())
                        // java variable scoping is awesome and doesn't allow me to name this `verb`
                        .map(verb1 -> this.story.verbs().get(verb1))
                )
                .orElse(defaultVerb);

        var errors = ofNullable(verb.errors())
                .orElseGet(defaultVerb::errors);

        return switch (kind) {
            case VERB -> ofNullable(errors.verb()).orElseGet(() -> defaultVerb.errors().verb());
            case OBJECT -> ofNullable(errors.object()).orElseGet(() -> defaultVerb.errors().object());
            case STATE -> ofNullable(errors.state()).orElseGet(() -> defaultVerb.errors().state());
        };
    }
}