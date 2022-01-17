package ch.bbw.m226.nils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record Story(String startRoom, Map<String, Room> rooms, Map<String, Verb> verbs,
                    List<Author> authors) {

    public Optional<String> synonymFor(String verb) {
        return this.verbs()
                .entrySet()
                .stream()
                .filter(entry -> Optional.ofNullable(entry.getValue().synonyms)
                        .map(synonyms -> synonyms.contains(verb))
                        .orElse(false)
                )
                .map(Map.Entry::getKey)
                .findFirst();
    }

    public record Room(String message, Map<String, Map<String, List<Action>>> verbs) {
    }

    public record Action(String message, String setState, String room, String ifState) {
    }

    public record Verb(Errors errors, List<String> synonyms) {
    }

    public record Errors(String verb, String object, String state) {
    }

    public record Author(String name, String function) {
    }
}
