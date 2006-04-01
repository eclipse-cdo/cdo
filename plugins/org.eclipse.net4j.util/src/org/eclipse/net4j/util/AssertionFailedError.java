package org.eclipse.net4j.util;


/**
 * <code>AssertionFailedException</code> is a runtime exception thrown
 * by some of the methods in <code>Assert</code>.
 * <p>
 * This class is not declared public to prevent some misuses; programs that catch 
 * or otherwise depend on assertion failures are susceptible to unexpected
 * breakage when assertions in the code are added or removed.
 * </p>
 */
class AssertionFailedError extends ImplementationError
{
  private static final long serialVersionUID = -1363257600902142582L;

  /** Constructs a new exception with the given message.
   */
  public AssertionFailedError(String detail)
  {
    super(detail);
  }
}
