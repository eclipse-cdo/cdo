/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;

/**
 * TODO Handle IORuntimeException
 * 
 * @author Eike Stepper
 */
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
