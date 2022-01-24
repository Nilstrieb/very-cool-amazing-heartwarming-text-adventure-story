package ch.bbw.m226.nils;

public class Colors {
    static final String RESET = "\u001b[0m";
    static final String RED = "\u001b[31m";
    static final String GREEN = "\u001b[32m";
    static final String BLUE = "\u001b[34m";
    static final String CYAN = "\u001b[1;31mm";

    public static String red(String msg) {
        return RED + msg + RESET;
    }

    public static String color(String msg, Color color) {
        var colorString = switch (color) {
            case NONE -> "";
            case RED -> RED;
            case GREEN -> GREEN;
            case CYAN -> CYAN;
            case BLUE -> BLUE;
        };

        return colorString + msg + RESET;
    }


    enum Color {
        NONE,
        RED,
        GREEN,
        CYAN, BLUE,
    }
}
