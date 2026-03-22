package exceptions;

/**
 * Exception thrown when a field contains invalid data.
 */
public class InvalidFieldException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new InvalidFieldException with a message.
     * @param message the detail message
     */
    public InvalidFieldException(String message) {
        super(message);
    }
}
