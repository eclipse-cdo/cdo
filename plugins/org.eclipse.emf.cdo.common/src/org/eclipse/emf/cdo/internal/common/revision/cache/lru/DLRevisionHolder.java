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

import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

/**
 * @author Eike Stepper
 */
public class DLRevisionHolder extends RevisionHolder
{
  private DLRevisionList dlList;

  private DLRevisionHolder dlPrev;

  private DLRevisionHolder dlNext;

  public DLRevisionHolder(DLRevisionList list, InternalCDORevision revision)
  {
    super(revision);
    dlList = list;
  }

  public DLRevisionList getDLList()
  {
    return dlList;
  }

  protected void setDLList(DLRevisionList list)
  {
    dlList = list;
  }

  public DLRevisionHolder getDLPrev()
  {
    return dlPrev;
  }

  public void setDLPrev(DLRevisionHolder prev)
  {
    dlPrev = prev;
  }

  public DLRevisionHolder getDLNext()
  {
    return dlNext;
  }

  public void setDLNext(DLRevisionHolder next)
  {
    dlNext = next;
  }
}
