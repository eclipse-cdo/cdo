/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.util;


/**
 * @author Simon McDuff
 */
public class PropertyChanged<T>
{
  Object notifier = new Object();
  
  T value;
  
  RuntimeException exception = null;
  
  public PropertyChanged(T value)
  {
    this.value = value;
  }
  
  public T get()
  {
    return value;
  }
  
  public void set(T newValue)
  {
    synchronized(notifier)
    {
      value = newValue;
      notifier.notifyAll();
    }
  }
  
  public void setException(RuntimeException exception)
  {
    synchronized(notifier)
    {
      this.exception = exception;
      notifier.notifyAll();
    }
  }
  
  public void acquire(Object accept, Object refuse)
  {
    acquire(accept, refuse, 0);
  }
  
  public void acquire(Object accept, Object refuse, long timeout)
  {
    synchronized(notifier)
    {      
      while (!equalToOneElement(accept, refuse))
      {
        try
        {
          notifier.wait(timeout);
        }
        catch (InterruptedException ex)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
  
  private boolean equalToOneElement(Object accept, Object refuse)
  {
    if (this.exception != null)
      throw this.exception;
    
    if (accept != null && accept.equals(value))
       return true;
    
    if (refuse != null && refuse.equals(value))
       throw new IllegalStateException();
    
     return false;
  }
  
}
