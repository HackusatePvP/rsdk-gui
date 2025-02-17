package me.piitex.rsdk.exceptions;

public class URLNotResolvedException extends Exception {
    private final String message;

    public URLNotResolvedException(String message) {
        super("Could not resolve host: " + message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
