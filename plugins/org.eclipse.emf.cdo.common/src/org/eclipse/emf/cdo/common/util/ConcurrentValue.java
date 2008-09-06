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
 * Allow synchronization between many threads for specific value.
 * <p>
 * e.g.: MainThread cv.set(1);<br>
 * Thread1 cv.acquire(3);<br>
 * Thread2 cv.acquire(4);<br>
 * Thread3 cv.acquire(100);<br>
 * Thread4 cv.acquire(new Object(){ boolean equals(Object other){other.equals(1) || other.equals(3) }});<br>
 * ...<br>
 * MainThread cv.set(3);// Thread 1 and 4 will unblock. Thread 2 and 3 will still be blocking. <br>
 * TODO Simon - Then we can move it to util.concurrent
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public final class ConcurrentValue<T>
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

  /**
   * Specify the new value.
   */
  public void set(T newValue)
  {
    synchronized (notifier)
    {
      value = newValue;
      notifier.notifyAll();
    }
  }

  /**
   * Reevaluate the condition. It is only useful if a thread is blocked at {@link ConcurrentValue#acquire()} and the
   * parameter passed changed. {@link ConcurrentValue#acquire()} generates a reevaluation automatically.
   */
  public void reevaluate()
  {
    synchronized (notifier)
    {
      notifier.notifyAll();
    }
  }

  /**
   * Blocking call.
   * <p>
   * Return when value accept is equal to {@link ConcurrentValue#get()}.
   */
  public void acquire(Object accept) throws InterruptedException
  {
    synchronized (notifier)
    {
      while (!equalToOneElement(accept))
      {
        notifier.wait();
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
