package exceptions;

/**
 * Exception thrown when a file fails to upload to the system.
 */
public class FileNotUploadedException extends Exception {
 private static final long serialVersionUID = 1L;
 
 /**
  * Default constructor.
  */
 public FileNotUploadedException()
  {
    super();
  }
  /**
   * Constructor with custom error message.
   * 
   * @param s the error message
   */
  public FileNotUploadedException(String s)
  {
    super(s);
  }
}
