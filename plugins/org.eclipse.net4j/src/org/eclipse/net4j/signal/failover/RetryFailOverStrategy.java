/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal.failover;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.internal.net4j.bundle.OM;

/**
 * @author Eike Stepper
 */
public class RetryFailOverStrategy extends NOOPFailOverStrategy
{
  /**
   * @since 2.0
   */
  public static final int RETRY_FOREVER = Integer.MAX_VALUE;

  private int retries;

  /**
   * @since 2.0
   */
  public RetryFailOverStrategy(IConnector connector, int retries)
  {
    super(connector);
    this.retries = retries;
  }

  public RetryFailOverStrategy(IConnector connector)
  {
    this(connector, RETRY_FOREVER);
  }

  /**
   * @since 2.0
   */
  public int getRetries()
  {
    return retries;
  }

  @Override
  public void handleFailOver(ISignalProtocol<?> protocol)
  {
    Exception exception = null;
    for (int i = 0; i < retries; i++)
    {
      try
      {
        handleOpen(protocol);
        return;
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        exception = ex;
      }
    }

    if (exception != null)
    {
      throw WrappedException.wrap(exception);
    }
  }
}
