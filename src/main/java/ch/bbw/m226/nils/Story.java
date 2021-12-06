package ch.bbw.m226.nils;

import java.util.List;
import java.util.Map;

public record Story(Map<String, Room> rooms) {

    public static record Room(String message, Map<String, Map<String, List<Action>>> verbs) {
    }

    public static record Action(String message, String setState, String goRoom, String ifState) {
    }
}
