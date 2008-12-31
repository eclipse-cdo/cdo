package org.eclipse.net4j.util.defs;

public class DefException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  public DefException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public DefException(Throwable cause)
  {
    super(cause);
  }

}
