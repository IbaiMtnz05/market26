package exceptions;

/**
 * Exception thrown when attempting to register a user with an email
 * that already exists in the system.
 */
public class UserAlreadyExistsException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new UserAlreadyExistsException with a message.
     * @param message the detail message
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
