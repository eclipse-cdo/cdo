package org.eclipse.net4j.util;

public class WrappedException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  private WrappedException(Exception exception)
  {
    super(exception);
  }

  public Exception exception()
  {
    return (Exception)getCause();
  }

  public static RuntimeException wrap(Exception exception)
  {
    if (exception instanceof RuntimeException)
    {
      return (RuntimeException)exception;
    }

    return new WrappedException(exception);
  }

  public static Exception unwrap(Exception exception)
  {
    if (exception instanceof WrappedException)
    {
      return ((WrappedException)exception).exception();
    }

    return exception;
  }
}
