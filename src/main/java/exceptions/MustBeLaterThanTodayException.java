package exceptions;

/**
 * Exception thrown when a sale publication date is set to a past date.
 * Sale dates must be today or in the future.
 */
public class MustBeLaterThanTodayException extends Exception {
 private static final long serialVersionUID = 1L;
 
 /**
  * Default constructor.
  */
 public MustBeLaterThanTodayException()
  {
    super();
  }
  /**
   * Constructor with custom error message.
   * 
   * @param s the error message
   */
  public MustBeLaterThanTodayException(String s)
  {
    super(s);
  }
}
