/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.List;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class CDOQueryImpl extends CDOQueryInfoImpl implements CDOQuery
{
  private InternalCDOView view;

  public CDOQueryImpl(InternalCDOView view, String queryLanguage, String queryString)
  {
    super(queryLanguage, queryString);
    this.view = view;
  }

  public InternalCDOView getView()
  {
    return view;
  }

  @Override
  public CDOQueryImpl setMaxResults(int maxResults)
  {
    this.maxResults = maxResults;
    return this;
  }

  public CDOQuery setParameter(String name, Object value)
  {
    parameters.put(name, value);
    return this;
  }

  @SuppressWarnings("unchecked")
  protected <T> AbstractQueryIterator<T> createQueryResult(Class<T> classObject)
  {
    CDOQueryInfoImpl queryInfo = createQueryInfo();
    if (classObject.equals(CDOID.class))
    {
      return new CDOQueryCDOIDIteratorImpl(view, queryInfo);
    }

    return new CDOQueryResultIteratorImpl<T>(view, queryInfo);
  }

  public <T> List<T> getResult(Class<T> classObject)
  {
    AbstractQueryIterator<T> queryResult = createQueryResult(classObject);
    view.getSession().getSessionProtocol().query(view.getViewID(), queryResult);
    return queryResult.asList();
  }

  public <T> BlockingCloseableIterator<T> getResultAsync(Class<T> classObject)
  {
    final AbstractQueryIterator<T> queryResult = createQueryResult(classObject);
    final Exception exception[] = new Exception[1];
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try
        {
          view.getSession().getSessionProtocol().query(view.getViewID(), queryResult);
        }
        catch (Exception ex)
        {
          queryResult.close();
          exception[0] = ex;
        }
      }
    };

    // TODO Simon: Can we leverage a thread pool?
    new Thread(runnable).start();

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

  protected CDOQueryInfoImpl createQueryInfo()
  {
    CDOQueryInfoImpl queryInfo = new CDOQueryInfoImpl(getQueryLanguage(), getQueryString());
    queryInfo.setMaxResults(getMaxResults());
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
      if (internalCDOObject.cdoID() == null)
      {
        throw new UnsupportedOperationException("Object not persisted");
      }

      if (internalCDOObject.cdoID().isTemporary())
      {
        throw new UnsupportedOperationException("Object not persisted");
      }

      return internalCDOObject.cdoID();
    }

    return object;
  }
}
