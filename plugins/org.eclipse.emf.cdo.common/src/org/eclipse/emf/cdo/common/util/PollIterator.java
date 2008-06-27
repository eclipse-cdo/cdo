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

import java.util.Iterator;

/**
 * @author Simon McDuff
 */
public class PollIterator<E> implements Iterator<E>
{
  private StateConcurrentQueue<E> queue;

  public PollIterator(StateConcurrentQueue<E> queue)
  {
    this.queue = queue;
  }

  public boolean hasNext()
  {
    while (true)
    {
      if (!queue.isEmpty()) return true;

      if (queue.isDone() && queue.isEmpty()) return false;

      try
      {
        Thread.sleep(10);
      }
      catch (InterruptedException ex)
      {
       Thread.currentThread().interrupt();
      }
    }

  }

  public E next()
  {
    return queue.poll();
  }

  public void remove()
  {
  }

}
