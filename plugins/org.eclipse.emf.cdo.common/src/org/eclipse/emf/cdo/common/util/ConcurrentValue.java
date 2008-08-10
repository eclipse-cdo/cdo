/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.common.util;

/**
 * TODO Simon: A bit of a documentation what this is good for. Then we can move it to util.concurrent
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class ConcurrentValue<T>
{
  private Object notifier = new Object();

  private T value;

  public ConcurrentValue(T value)
  {
    this.value = value;
  }

  public T get()
  {
    return value;
  }

  public void set(T newValue)
  {
    synchronized (notifier)
    {
      value = newValue;
      notifier.notifyAll();
    }
  }

  public void reevaluate()
  {
    synchronized (notifier)
    {
      notifier.notifyAll();
    }
  }

  public void acquire(Object accept)
  {
    synchronized (notifier)
    {
      while (!equalToOneElement(accept))
      {
        try
        {
          notifier.wait();
        }
        catch (InterruptedException ex)
        {
          // TODO Simon: This construct is strange. Why not just propagate the IE?
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private boolean equalToOneElement(Object accept)
  {
    if (accept != null && accept.equals(value))
    {
      return true;
    }

    return false;
  }

}
