package schemely.scheme;

/**
 * @author Colin Fleming
 */
public class REPLException extends Exception
{
  public REPLException()
  {
  }

  public REPLException(String message)
  {
    super(message);
  }

  public REPLException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public REPLException(Throwable cause)
  {
    super(cause);
  }
}
