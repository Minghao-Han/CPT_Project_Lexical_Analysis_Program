package Exceptions;

public class EofException extends Exception{
    public EofException() {}

    public EofException(String message) {
        super(message);
    }

    public EofException(Throwable cause) {
        super(cause);
    }

    public EofException(String message, Throwable cause) {
        super(message, cause);
    }
}
