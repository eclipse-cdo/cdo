/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Simon McDuff
 * @since 2.0
 * @deprecated Use a callback-based approach
 */
@Deprecated
public class MEMStoreQueryIterator implements CloseableIterator<Object>
{
  private MEMStore store;

  private Iterator<CDORevision> revisions;

  private Object nextObject;

  private ArrayList<Object> filters = new ArrayList<Object>();

  public MEMStoreQueryIterator(MEMStore memStore)
  {
    store = memStore;
    revisions = store.getCurrentRevisions().iterator();
  }

  public void addFilter(Object filter)
  {
    filters.add(filter);
  }

  public void activate()
  {
    nextObject = nextObject();
  }

  public boolean hasNext()
  {
    return nextObject != null;
  }

  public Object next()
  {
    try
    {
      if (nextObject == null)
      {
        throw new NoSuchElementException();
      }

      return nextObject;
    }
    finally
    {
      nextObject = nextObject();
    }
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  public void close()
  {
  }

  public boolean isClosed()
  {
    return false;
  }

  private Object nextObject()
  {
    nextObject = null;
    while (revisions.hasNext() && nextObject == null)
    {
      nextObject = revisions.next();
      for (Object filter : filters)
      {
        if (!filter.equals(nextObject))
        {
          nextObject = null;
          break;
        }
      }
    }

    return nextObject;
  }
}
