package xyz.metratrj.jbyteinspector.model;

/**
 * Exception thrown when a malformed class file is encountered.
 */
public class BytecodeException extends RuntimeException {
    public BytecodeException(String message) {
        super(message);
    }

    public BytecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
