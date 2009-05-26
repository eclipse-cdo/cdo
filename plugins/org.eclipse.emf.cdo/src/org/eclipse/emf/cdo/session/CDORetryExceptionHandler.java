/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.spi.cdo.CDOSessionProtocol;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDORetryExceptionHandler implements CDOSessionProtocol.ExceptionHandler
{
  public static final int RETRY_FOREVER = -1;

  private int retries;

  public CDORetryExceptionHandler(int retries)
  {
    this.retries = retries;
  }

  public CDORetryExceptionHandler()
  {
    this(RETRY_FOREVER);
  }

  public int getRetries()
  {
    return retries;
  }

  public boolean isRetryingForever()
  {
    return retries < 0;
  }

  public void handleException(int attempt, Exception exception) throws Exception
  {
    int max = isRetryingForever() ? Integer.MAX_VALUE : retries;
    if (attempt > max)
    {
      throw exception;
    }
  }

  @Override
  public String toString()
  {
    Object arg = isRetryingForever() ? "forever" : retries;
    return MessageFormat.format("CDORetryExceptionHandler[retries={0}]", arg); //$NON-NLS-1$
  }
}
