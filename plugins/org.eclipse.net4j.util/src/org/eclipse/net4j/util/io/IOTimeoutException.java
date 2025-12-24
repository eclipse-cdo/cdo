/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class IOTimeoutException extends IOException
{
  private static final long serialVersionUID = 1L;

  public IOTimeoutException()
  {
  }

  public IOTimeoutException(String message, Throwable cause)
  {
    super(message);
    initCause(cause);
  }

  public IOTimeoutException(String message)
  {
    super(message);
  }

  public IOTimeoutException(Throwable cause)
  {
    initCause(cause);
  }

  public TimeoutException createTimeoutException()
  {
    TimeoutException timeoutException = new TimeoutException(getMessage());
    timeoutException.initCause(this);
    return timeoutException;
  }
}
