package ch.bbw.m226.nils;

public class ActionNotFoundException extends Exception {
    private final NotFoundKind kind;

    public ActionNotFoundException(NotFoundKind kind) {
        this.kind = kind;
    }

    public NotFoundKind kind() {
        return this.kind;
    }

    enum NotFoundKind {
        VERB,
        OBJECT,
        STATE,
    }
}
