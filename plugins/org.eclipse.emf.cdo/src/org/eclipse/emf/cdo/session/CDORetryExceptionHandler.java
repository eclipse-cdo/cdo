/*
 * Copyright (c) 2009-2012, 2014, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;

import java.text.MessageFormat;

/**
 * A {@link ExceptionHandler session exection handler} that retries the failed protocol operation a configurable number
 * of times.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class CDORetryExceptionHandler implements CDOSession.ExceptionHandler
{
  /**
   * The value used to retry a failed operation without a retry limit.
   */
  public static final int RETRY_FOREVER = -1;

  private int retries;

  /**
   * Creates a retry handler with the given retry limit.
   *
   * @param retries the maximum number of retries, or a negative value to retry forever.
   */
  public CDORetryExceptionHandler(int retries)
  {
    this.retries = retries;
  }

  /**
   * Creates a retry handler that retries forever.
   */
  public CDORetryExceptionHandler()
  {
    this(RETRY_FOREVER);
  }

  /**
   * Returns the configured retry limit.
   *
   * @return the retry limit.
   */
  public int getRetries()
  {
    return retries;
  }

  /**
   * Returns whether this handler retries without a limit.
   *
   * @return <code>true</code> if retries are unlimited.
   */
  public boolean isRetryingForever()
  {
    return retries < 0;
  }

  /**
   * @since 4.0
   */
  @Override
  public void handleException(CDOSession session, int attempt, Exception exception) throws Exception
  {
    int max = isRetryingForever() ? Integer.MAX_VALUE : retries;
    if (attempt > max)
    {
      throw exception;
    }
  }

  /**
   * Returns a concise representation of this retry handler.
   *
   * @return the handler description.
   */
  @Override
  public String toString()
  {
    Object arg = isRetryingForever() ? "forever" : retries;
    return MessageFormat.format("CDORetryExceptionHandler[retries={0}]", arg); //$NON-NLS-1$
  }
}
