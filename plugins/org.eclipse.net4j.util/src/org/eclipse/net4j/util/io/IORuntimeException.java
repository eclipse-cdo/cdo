/*
 * Copyright (c) 2007-2009, 2011, 2012, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;

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

  /**
   * @since 3.16
   */
  public void rethrow() throws IOException
  {
    Throwable cause = getCause();
    if (cause instanceof IOException)
    {
      throw (IOException)cause;
    }

    throw this;
  }
}
