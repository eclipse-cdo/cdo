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

import org.eclipse.emf.cdo.common.query.ResultWriterQueue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Simon McDuff
 */
public class StateConcurrentQueue<E> extends ConcurrentLinkedQueue<E> implements ResultWriterQueue<E>
{
  private static final long serialVersionUID = 1L;
  
  private boolean done = false;

  private RuntimeException exception = null;
 
  public StateConcurrentQueue()
  {
  }

  public void setRuntimeException(RuntimeException exception)
  {
    this.exception = exception;
  }
  
  public E poll()
  {
    if (exception != null) throw exception;

    return super.poll();
  }

  public boolean isDone()
  {
    return done;
  }

  public void release()
  {
    done = true; 
  }

  public void setException(RuntimeException exception)
  {
    this.exception = exception;
  }
  
  public void isReleased()
  {
    synchronized(this)
    {
      
    }
  }
  
}
