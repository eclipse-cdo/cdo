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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class MEMStoreQueryIterator implements CloseableIterator<Object>
{
  private MEMStore store = null;

  Iterator<CDORevision> revisions;

  Object nextObject = null;

  ArrayList<Object> filters = new ArrayList<Object>();

  public MEMStoreQueryIterator(MEMStore memStore)
  {
    store = memStore;
    revisions = store.getRevisions().iterator();
  }

  public void addFilter(Object filter)
  {
    filters.add(filter);
  }

  public void activate()
  {
    nextObject = nextObject();
  }

  public Object nextObject()
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

  public boolean hasNext()
  {
    try
    {
      // @TODO only for test purpose need to be removed.
      Thread.sleep(500);
    }
    catch (InterruptedException ex)
    {
      Thread.currentThread().interrupt();
    }

    return nextObject != null;
  }

  public Object next()
  {
    try
    {
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

}
