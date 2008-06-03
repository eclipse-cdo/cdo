/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class LRURevisionList extends DLRevisionList
{
  private int capacity;

  public LRURevisionList(int capacity)
  {
    this.capacity = capacity;
  }

  public int capacity()
  {
    return capacity;
  }

  public synchronized void capacity(int capacity)
  {
    this.capacity = capacity;
    eviction();
  }

  @Override
  public synchronized void add(DLRevisionHolder holder)
  {
    addHead(holder);
    eviction();
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("LRURevisionList[size={0}, capacity={1}]", size(), capacity);
  }

  protected void eviction()
  {
    if (capacity != 0)
    {
      while (size() > capacity)
      {
        evict((LRURevisionHolder)getDLTail());
      }
    }
  }

  protected void evict(LRURevisionHolder holder)
  {
    remove(holder);
  }
}
