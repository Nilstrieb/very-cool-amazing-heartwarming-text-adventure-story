package ch.bbw.m226.nils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Story(Map<String, Room> rooms) {

    public record Room(String message, Map<String, LinkedHashMap<String, List<Action>>> verbs) {
    }

    public record Action(String message, String setState, String goRoom, String ifState) {
    }
}
