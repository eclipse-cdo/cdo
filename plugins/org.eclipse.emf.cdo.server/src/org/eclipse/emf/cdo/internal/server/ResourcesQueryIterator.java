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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.NoSuchElementException;

/**
 * @author Eike Stepper
 */
public class ResourcesQueryIterator implements CloseableIterator<Object>
{
  private String query;

  private Object nextObject;

  public ResourcesQueryIterator(String query)
  {
    this.query = query;
  }

  public String getQuery()
  {
    return query;
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
    // while (revisions.hasNext() && nextObject == null)
    // {
    // nextObject = revisions.next();
    // for (Object filter : filters)
    // {
    // if (!filter.equals(nextObject))
    // {
    // nextObject = null;
    // break;
    // }
    // }
    // }

    return nextObject;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends QueryIteratorFactory
  {
    public static final String TYPE = "resources";

    public Factory()
    {
      super(TYPE);
    }

    public CloseableIterator<Object> create(String description) throws ProductCreationException
    {
      return new ResourcesQueryIterator(description);
    }

    @SuppressWarnings("unchecked")
    public static CloseableIterator<Object> get(IManagedContainer container, String queryLanguage, String queryString)
    {
      return (CloseableIterator<Object>)container.getElement(PRODUCT_GROUP, TYPE, queryString);
    }
  }
}
