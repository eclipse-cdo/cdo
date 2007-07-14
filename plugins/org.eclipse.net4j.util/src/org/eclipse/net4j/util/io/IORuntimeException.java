package org.eclipse.net4j.util.io;

/**
 * @author Eike Stepper
 */
public class IORuntimeException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public IORuntimeException()
  {
  }

  public IORuntimeException(String message)
  {
    super(message);
  }

  public IORuntimeException(Throwable cause)
  {
    super(cause);
  }

  public IORuntimeException(String message, Throwable cause)
  {
    super(message, cause);
  }
}