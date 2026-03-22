package exceptions;

/**
 * Exception thrown when a password does not meet security requirements.
 */
public class WeakPasswordException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new WeakPasswordException with a message.
     * @param message the detail message
     */
    public WeakPasswordException(String message) {
        super(message);
    }
}
