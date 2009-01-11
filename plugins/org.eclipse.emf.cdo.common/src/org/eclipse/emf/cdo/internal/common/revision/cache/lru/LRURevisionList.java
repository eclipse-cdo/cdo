/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision.cache.lru;

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

  /**
   * Sets the capacity of LRU cache revisions. A value of zero disables eviction completely such that the cache will
   * grow indefinetely.
   */
  public void capacity(int capacity)
  {
    this.capacity = capacity;
    eviction();
  }

  @Override
  public void add(DLRevisionHolder holder)
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
