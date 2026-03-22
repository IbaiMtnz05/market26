package exceptions;

/**
 * Exception thrown when attempting to create a sale with a title that already exists
 * for the same seller.
 */
public class SaleAlreadyExistException extends Exception {
 private static final long serialVersionUID = 1L;
 
 /**
  * Default constructor.
  */
 public SaleAlreadyExistException()
  {
    super();
  }
  /**
   * Constructor with custom error message.
   * 
   * @param s the error message
   */
  public SaleAlreadyExistException(String s)
  {
    super(s);
  }
}