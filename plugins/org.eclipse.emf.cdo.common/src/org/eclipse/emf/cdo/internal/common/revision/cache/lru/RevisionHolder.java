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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class RevisionHolder
{
  private RevisionHolder prev;

  private RevisionHolder next;

  private InternalCDORevision revision;

  public RevisionHolder(InternalCDORevision revision)
  {
    setRevision(revision);
  }

  public CDOID getID()
  {
    return revision.getID();
  }

  public int getVersion()
  {
    return revision.getVersion();
  }

  public long getCreated()
  {
    return revision.getCreated();
  }

  public long getRevised()
  {
    return revision.getRevised();
  }

  public boolean isCurrent()
  {
    return getRevised() == CDORevision.UNSPECIFIED_DATE;
  }

  public boolean isValid(long timeStamp)
  {
    return (getRevised() == CDORevision.UNSPECIFIED_DATE || getRevised() >= timeStamp) && timeStamp >= getCreated();
  }

  /**
   * Returns:
   * <ul>
   * <li>-1 if the revision is valid *before* the timestamp
   * <li>0 if the revision is valid *at* the timestamp
   * <li>1 if the revision is valid *after* the timestamp
   * </ul>
   */
  public int compareTo(long timeStamp)
  {
    if (timeStamp < getCreated())
    {
      return 1;
    }

    long revised = getRevised();
    if (revised != CDORevision.UNSPECIFIED_DATE && revised < timeStamp)
    {
      return -1;
    }

    return 0;
  }

  public RevisionHolder getPrev()
  {
    return prev;
  }

  public void setPrev(RevisionHolder prev)
  {
    this.prev = prev;
  }

  public RevisionHolder getNext()
  {
    return next;
  }

  public void setNext(RevisionHolder next)
  {
    this.next = next;
  }

  public boolean isLoaded()
  {
    return revision != null;
  }

  public InternalCDORevision getRevision()
  {
    return revision;
  }

  public void setRevision(InternalCDORevision revision)
  {
    this.revision = revision;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("RevisionHolder[{0}]", revision);
  }
}
