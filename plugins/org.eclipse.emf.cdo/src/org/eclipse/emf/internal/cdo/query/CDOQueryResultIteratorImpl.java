/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends AbstractQueryIterator<T>
{
  public CDOQueryResultIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo);
  }

  @Override
  public T next()
  {
    return adapt(super.next());
  }

  @Override
  public List<T> asList()
  {
    List<Object> result = new ArrayList<>();
    while (super.hasNext())
    {
      result.add(super.next());
    }

    return new QueryResultList(result);
  }

  @Override
  public T asValue()
  {
    if (hasNext())
    {
      return next();
    }

    return null;
  }

  @SuppressWarnings("unchecked")
  protected T adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      CDOID id = (CDOID)object;
      if (id.isNull())
      {
        return null;
      }

      CDOView view = getView();

      try
      {
        CDOObject cdoObject = view.getObject(id, true);
        return (T)CDOUtil.getEObject(cdoObject);
      }
      catch (ObjectNotFoundException ex)
      {
        if (view instanceof InternalCDOTransaction)
        {
          InternalCDOTransaction transaction = (InternalCDOTransaction)view;
          CDOObject cdoObject = transaction.getLastSavepoint().getDetachedObject(id);
          return (T)CDOUtil.getEObject(cdoObject);
        }

        return null;
      }
    }

    // Support a query return value of Object[]
    if (object instanceof Object[])
    {
      Object[] objects = (Object[])object;
      Object[] resolvedObjects = new Object[objects.length];
      for (int i = 0; i < objects.length; i++)
      {
        if (objects[i] instanceof CDOID)
        {
          resolvedObjects[i] = adapt(objects[i]);
        }
        else
        {
          resolvedObjects[i] = objects[i];
        }
      }

      return (T)resolvedObjects;
    }

    return (T)object;
  }

  /**
   * @author Simon McDuff
   */
  private class QueryResultList extends AbstractList<T> implements EList<T>
  {
    private List<Object> objects;

    public QueryResultList(List<Object> objects)
    {
      this.objects = objects;
    }

    @Override
    public T get(int index)
    {
      return adapt(objects.get(index));
    }

    @Override
    public int size()
    {
      return objects.size();
    }

    @Override
    public String toString()
    {
      return "QueryResultList" + objects.toString();
    }

    @Override
    public void move(int newPosition, T object)
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public T move(int newPosition, int oldPosition)
    {
      throw new UnsupportedOperationException();
    }
  }
}
