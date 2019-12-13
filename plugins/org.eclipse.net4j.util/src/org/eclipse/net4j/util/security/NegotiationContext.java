/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.WrappedException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class NegotiationContext implements INegotiationContext
{
  private Receiver receiver;

  private Enum<?> state;

  private Object info;

  private CountDownLatch finishedLatch = new CountDownLatch(1);

  public NegotiationContext()
  {
  }

  @Override
  public Receiver getReceiver()
  {
    return receiver;
  }

  @Override
  public void setReceiver(Receiver receiver)
  {
    this.receiver = receiver;
  }

  @Override
  public Enum<?> getState()
  {
    return state;
  }

  @Override
  public void setState(Enum<?> state)
  {
    this.state = state;
  }

  @Override
  public Object getInfo()
  {
    return info;
  }

  @Override
  public void setInfo(Object info)
  {
    this.info = info;
  }

  @Override
  public void setFinished(boolean success)
  {
    if (finishedLatch != null)
    {
      finishedLatch.countDown();
    }
  }

  @Override
  public Enum<?> waitUntilFinished(long timeout)
  {
    if (finishedLatch == null)
    {
      throw new IllegalStateException("finishedLatch == null"); //$NON-NLS-1$
    }

    try
    {
      if (timeout == NO_TIMEOUT)
      {
        finishedLatch.await();
      }
      else
      {
        finishedLatch.await(timeout, TimeUnit.MILLISECONDS);
      }
    }
    catch (InterruptedException ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      finishedLatch = null;
    }

    return state;
  }
}
