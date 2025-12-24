/*
 * Copyright (c) 2007, 2008, 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * An unchecked exception that wraps a checked exception. Wrapping occurs conditionally in the static
 * {@link #wrap(Exception, String) wrap()} methods.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class WrappedException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  private WrappedException(Exception exception)
  {
    super(exception);
  }

  private WrappedException(String message, Exception exception)
  {
    super(message, exception);
  }

  public Exception exception()
  {
    return (Exception)getCause();
  }

  /**
   * @since 3.1
   */
  public static RuntimeException wrap(Exception exception, String message)
  {
    return new WrappedException(message, exception);
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
