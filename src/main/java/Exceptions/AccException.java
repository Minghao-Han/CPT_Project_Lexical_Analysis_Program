package Exceptions;

public class AccException extends Exception {
    public AccException() {}

    public AccException(String message) {
        super(message);
    }

    public AccException(Throwable cause) {
        super(cause);
    }

    public AccException(String message, Throwable cause) {
        super(message, cause);
    }
}
