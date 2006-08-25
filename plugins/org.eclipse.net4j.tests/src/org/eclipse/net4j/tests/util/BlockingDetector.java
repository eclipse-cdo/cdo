/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests.util;


import junit.framework.Assert;


public abstract class BlockingDetector
{
  public static final long DEFAULT_WAIT_MILLIS = 100;

  protected Thread thread;

  protected BlockableOperationException exception;

  public BlockingDetector(Object target, Object monitor, boolean blockingExpected) throws Throwable
  {
    this(target, monitor, blockingExpected, DEFAULT_WAIT_MILLIS);
  }

  public BlockingDetector(Object target, Object monitor, boolean blockingExpected, long waitMillis)
      throws Throwable
  {
    startOperation(target);

    try
    {
      Thread.sleep(waitMillis);
    }
    catch (InterruptedException ex)
    {
      throw new BlockableOperationException(ex);
    }

    if (exception != null)
    {
      throw exception.getCause();
    }

    boolean alive = thread.isAlive();

    if (alive) synchronized (monitor)
    {
      monitor.notifyAll();
    }

    if (blockingExpected)
    {
      Assert.assertTrue("blocking expected", alive);
    }
    else
    {
      Assert.assertTrue("no blocking expected", !alive);
    }
  }

  public void startOperation(final Object target)
  {
    thread = new Thread()
    {

      public void run()
      {
        try
        {
          blockableOperation(target);
        }
        catch (Exception ex)
        {
          exception = new BlockableOperationException(ex);
          throw exception;
        }
      }
    };

    thread.start();
  }

  protected abstract void blockableOperation(Object target) throws Exception;


  private static class BlockableOperationException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    public BlockableOperationException(Exception ex)
    {
      super(ex);
    }
  }
}
