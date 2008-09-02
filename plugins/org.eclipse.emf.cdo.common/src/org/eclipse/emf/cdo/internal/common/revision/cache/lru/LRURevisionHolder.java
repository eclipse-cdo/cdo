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
package org.eclipse.emf.cdo.internal.common.revision.cache.lru;

import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

/**
 * @author Eike Stepper
 */
public class LRURevisionHolder extends DLRevisionHolder
{
  private long usedStamp;

  public LRURevisionHolder(LRURevisionList list, InternalCDORevision revision)
  {
    super(list, revision);
    usedStamp = System.currentTimeMillis();
  }

  @Override
  public LRURevisionList getDLList()
  {
    return (LRURevisionList)super.getDLList();
  }

  public long getUsedStamp()
  {
    return usedStamp;
  }

  @Override
  public InternalCDORevision getRevision()
  {
    stamp();
    return super.getRevision();
  }

  protected void stamp()
  {
    usedStamp = System.currentTimeMillis();
    LRURevisionList list = getDLList();
    if (list != null)
    {
      synchronized (list)
      {
        list.remove(this);
        list.addHead(this);
      }
    }
  }
}
