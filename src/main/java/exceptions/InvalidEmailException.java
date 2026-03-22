package exceptions;

/**
 * Exception thrown when an email address format is invalid.
 */
public class InvalidEmailException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new InvalidEmailException with a message.
     * @param message the detail message
     */
    public InvalidEmailException(String message) {
        super(message);
    }
}
