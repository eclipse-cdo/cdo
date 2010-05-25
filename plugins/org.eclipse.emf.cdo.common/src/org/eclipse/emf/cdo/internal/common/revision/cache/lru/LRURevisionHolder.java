/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision.cache.lru;

import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class LRURevisionHolder extends DLRevisionHolder
{
  private long usedStamp;

  public LRURevisionHolder(InternalCDORevision revision)
  {
    super(null, revision);
    usedStamp = System.currentTimeMillis();
  }

  @Override
  public LRURevisionList getDLList()
  {
    return (LRURevisionList)super.getDLList();
  }

  @Override
  protected void setDLList(DLRevisionList list)
  {
    if (list == null || list instanceof LRURevisionList)
    {
      super.setDLList(list);
    }
    else
    {
      throw new IllegalArgumentException(MessageFormat.format(
          Messages.getString("LRURevisionHolder.0"), LRURevisionList.class.getName(), list)); //$NON-NLS-1$ 
    }
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
