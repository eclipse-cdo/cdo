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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class DLRevisionList extends DLRevisionHolder
{
  private int size;

  public DLRevisionList()
  {
    super(null, null);
    setDLHead(this);
    setDLTail(this);
  }

  public int size()
  {
    return size;
  }

  public DLRevisionHolder getDLHead()
  {
    return getDLNext();
  }

  public void setDLHead(DLRevisionHolder head)
  {
    setDLNext(head);
  }

  public DLRevisionHolder getDLTail()
  {
    return getDLPrev();
  }

  public void setDLTail(DLRevisionHolder tail)
  {
    setDLPrev(tail);
  }

  public DLRevisionHolder get(int index)
  {
    if (index < 0 || index >= size)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    DLRevisionHolder holder = this;
    if (index < size >> 1)
    {
      for (int i = 0; i <= index; i++)
      {
        holder = holder.getDLNext();
      }
    }
    else
    {
      for (int i = size; i > index; i--)
      {
        holder = holder.getDLPrev();
      }
    }

    return holder;
  }

  public void add(DLRevisionHolder holder)
  {
    addTail(holder);
  }

  public synchronized void addHead(DLRevisionHolder holder)
  {
    ++size;
    DLRevisionHolder head = getDLHead();
    head.setDLPrev(holder);
    holder.setDLNext(head);
    holder.setDLPrev(this);
    setDLHead(holder);
  }

  public synchronized void addTail(DLRevisionHolder holder)
  {
    ++size;
    DLRevisionHolder tail = getDLTail();
    tail.setDLNext(holder);
    holder.setDLPrev(tail);
    holder.setDLNext(this);
    setDLTail(holder);
  }

  public synchronized void remove(DLRevisionHolder holder)
  {
    --size;
    DLRevisionHolder prev = holder.getDLPrev();
    DLRevisionHolder next = holder.getDLNext();

    prev.setDLNext(next);
    holder.setDLPrev(null);
    holder.setDLNext(null);
    next.setDLPrev(prev);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DLRevisionList[size={0}]", size);
  }

  @Override
  public int compareTo(long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getCreated()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public DLRevisionList getDLList()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOID getID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public RevisionHolder getNext()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public RevisionHolder getPrev()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getRevised()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDORevision getRevision(boolean loadOnDemand)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getVersion()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isCurrent()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isLoaded()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isValid(long timeStamp)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected InternalCDORevision loadRevision()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setNext(RevisionHolder next)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setPrev(RevisionHolder prev)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRevision(CDORevision revision)
  {
    // Ignore
  }
}
