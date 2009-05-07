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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

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

  @Override
  protected void setDLList(DLRevisionList list)
  {
    if (getPrev() != null || getDLNext() != null || getDLList() != null)
    {
      throw new IllegalStateException(Messages.getString("DLRevisionList.0")); //$NON-NLS-1$
    }

    super.setDLList(list);
  }

  public void setDLTail(DLRevisionHolder tail)
  {
    setDLPrev(tail);
  }

  public DLRevisionHolder get(int index)
  {
    if (index < 0 || index >= size)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size); //$NON-NLS-1$ //$NON-NLS-2$
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

  protected void validateUnlink(DLRevisionHolder holder)
  {
    if (holder.getDLList() != null)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("DLRevisionList.3"), holder)); //$NON-NLS-1$ 
    }
  }

  protected void validateLink(DLRevisionHolder holder)
  {
    if (holder.getDLList() != this)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("DLRevisionList.5"), holder)); //$NON-NLS-1$
    }
  }

  public void addHead(DLRevisionHolder holder)
  {
    validateUnlink(holder);

    ++size;
    DLRevisionHolder head = getDLHead();
    head.setDLPrev(holder);
    holder.setDLNext(head);
    holder.setDLPrev(this);
    holder.setDLList(this);
    setDLHead(holder);
  }

  public void addTail(DLRevisionHolder holder)
  {
    validateUnlink(holder);

    ++size;
    DLRevisionHolder tail = getDLTail();
    tail.setDLNext(holder);
    holder.setDLPrev(tail);
    holder.setDLNext(this);
    holder.setDLList(this);
    setDLTail(holder);
  }

  public void remove(DLRevisionHolder holder)
  {
    validateLink(holder);

    --size;
    DLRevisionHolder prev = holder.getDLPrev();
    DLRevisionHolder next = holder.getDLNext();

    prev.setDLNext(next);
    holder.setDLPrev(null);
    holder.setDLNext(null);
    holder.setDLList(null);
    next.setDLPrev(prev);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("DLRevisionList[size={0}]", size); //$NON-NLS-1$
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
  public InternalCDORevision getRevision()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setRevision(InternalCDORevision revision)
  {
    // Ignore
  }
}
