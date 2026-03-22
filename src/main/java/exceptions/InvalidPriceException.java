package exceptions;

/**
 * Exception thrown when a price is invalid or does not meet business rules.
 */
public class InvalidPriceException extends Exception {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new InvalidPriceException with a message.
     * @param message the detail message
     */
    public InvalidPriceException(String message) {
        super(message);
    }
}
