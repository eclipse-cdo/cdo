/*
 * Copyright (c) 2008-2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.BlockingCloseableIterator;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.view.CDOQuery;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.List;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class CDOQueryImpl extends CDOQueryInfoImpl implements CDOQuery
{
  private static final String OBJECT_NOT_PERSISTED_MESSAGE = Messages.getString("CDOQueryImpl.0"); //$NON-NLS-1$

  private InternalCDOView view;

  public CDOQueryImpl(InternalCDOView view, String queryLanguage, String queryString, Object context)
  {
    super(queryLanguage, queryString, context);
    this.view = view;
  }

  @Override
  public InternalCDOView getView()
  {
    return view;
  }

  @Override
  public CDOQueryImpl setContext(Object context)
  {
    this.context = context;
    return this;
  }

  @Override
  public CDOQueryImpl setParameter(String name, Object value)
  {
    parameters.put(name, value);
    return this;
  }

  @Override
  public CDOQueryImpl unsetParameter(String name)
  {
    parameters.remove(name);
    return this;
  }

  @Override
  public CDOQueryImpl setMaxResults(int maxResults)
  {
    this.maxResults = maxResults;
    return this;
  }

  protected <T> AbstractQueryIterator<T> createQueryResult(Class<T> type)
  {
    CDOQueryInfoImpl queryInfo = createQueryInfo();
    if (CDOID.class.equals(type))
    {
      return new CDOQueryCDOIDIteratorImpl<>(view, queryInfo);
    }

    return new CDOQueryResultIteratorImpl<>(view, queryInfo);
  }

  @Override
  public <T> List<T> getResult(Class<T> type)
  {
    AbstractQueryIterator<T> queryResult = null;

    try
    {
      queryResult = createQueryResult(type);
      view.getSession().getSessionProtocol().query(view, queryResult);
      return queryResult.asList();
    }
    finally
    {
      if (queryResult != null)
      {
        queryResult.close();
      }
    }
  }

  @Override
  public <T> List<T> getResult()
  {
    return getResult(null);
  }

  @Override
  public <T> T getResultValue(Class<T> type)
  {
    AbstractQueryIterator<T> queryResult = null;

    try
    {
      queryResult = createQueryResult(type);
      view.getSession().getSessionProtocol().query(view, queryResult);
      return queryResult.asValue();
    }
    finally
    {
      if (queryResult != null)
      {
        queryResult.close();
      }
    }
  }

  @Override
  public <T> T getResultValue()
  {
    return getResultValue(null);
  }

  @Override
  public <T> BlockingCloseableIterator<T> getResultAsync(Class<T> classObject)
  {
    final AbstractQueryIterator<T> queryResult = createQueryResult(classObject);
    final Exception exception[] = new Exception[1];
    Runnable runnable = new Runnable()
    {
      @Override
      public void run()
      {
        try
        {
          CDOSessionProtocol sessionProtocol = view.getSession().getSessionProtocol();
          sessionProtocol.query(view, queryResult);
        }
        catch (Exception ex)
        {
          queryResult.close();
          exception[0] = ex;
        }
      }
    };

    ConcurrencyUtil.execute(view, runnable);

    try
    {
      queryResult.waitForInitialization();
    }
    catch (Exception ex)
    {
      exception[0] = ex;
    }

    if (exception[0] != null)
    {
      throw WrappedException.wrap(exception[0]);
    }

    return queryResult;
  }

  @Override
  public <T> BlockingCloseableIterator<T> getResultAsync()
  {
    return getResultAsync(null);
  }

  protected CDOQueryInfoImpl createQueryInfo()
  {
    CDOQueryInfoImpl queryInfo = new CDOQueryInfoImpl(getQueryLanguage(), getQueryString(), getContext());
    queryInfo.setMaxResults(getMaxResults());
    queryInfo.setChangeSetData(getChangeSetData());

    for (Entry<String, Object> entry : getParameters().entrySet())
    {
      Object value = entry.getValue();
      value = adapt(value);
      queryInfo.addParameter(entry.getKey(), value);
    }

    return queryInfo;
  }

  protected Object adapt(Object object)
  {
    if (object instanceof InternalCDOObject)
    {
      InternalCDOObject internalCDOObject = FSMUtil.adapt(object, view);
      CDOID id = internalCDOObject.cdoID();
      if (id == null)
      {
        throw new UnsupportedOperationException(OBJECT_NOT_PERSISTED_MESSAGE);
      }

      if (view.isObjectNew(id))
      {
        throw new UnsupportedOperationException(OBJECT_NOT_PERSISTED_MESSAGE);
      }

      return id;
    }

    return object;
  }
}
