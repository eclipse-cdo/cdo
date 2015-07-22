/*
 * Copyright (c) 2007-2009, 2011-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public final class ConcurrencyUtil
{
  private ConcurrencyUtil()
  {
  }

  public static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      return;
    }
  }

  public static void sleep(long millis, int nanos)
  {
    try
    {
      Thread.sleep(millis, nanos);
    }
    catch (InterruptedException ex)
    {
      return;
    }
  }

  /**
   * @since 3.5
   */
  public static ExecutorService getExecutorService(IManagedContainer container)
  {
    return ExecutorServiceFactory.get(container);
  }

  /**
   * @since 3.6
   */
  public static ExecutorService getExecutorService(Object object)
  {
    if (object instanceof IExecutorServiceProvider)
    {
      try
      {
        return ((IExecutorServiceProvider)object).getExecutorService();
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    if (object instanceof IManagedContainer)
    {
      try
      {
        return getExecutorService((IManagedContainer)object);
      }
      catch (Exception ex)
      {
        //$FALL-THROUGH$
      }
    }

    return null;
  }
}
