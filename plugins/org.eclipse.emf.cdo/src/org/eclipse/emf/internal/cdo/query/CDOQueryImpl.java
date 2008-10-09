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
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.util.BlockingCloseableIterator;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.query.CDOQuery;

import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.protocol.QueryRequest;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class CDOQueryImpl extends CDOQueryInfoImpl implements CDOQuery
{
  private CDOViewImpl view;

  public CDOQueryImpl(CDOViewImpl view, String queryLanguage, String queryString)
  {
    super(queryLanguage, queryString);
    this.view = view;
  }

  public CDOView getView()
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

  public <T> List<T> getResult(Class<T> classObject)
  {
    CDOQueryInfoImpl queryInfo = createQueryInfo();
    CDOQueryResultIteratorImpl<T> queryResult = new CDOQueryResultIteratorImpl<T>(view, queryInfo);

    try
    {
      CDOSession session = view.getSession();
      IChannel channel = session.getChannel();
      QueryRequest request = new QueryRequest(channel, view.getViewID(), queryInfo, queryResult);
      session.getFailOverStrategy().send(request);
    }
    catch (Exception exception)
    {
      throw WrappedException.wrap(exception);
    }

    return queryResult.getAsList();
  }

  public <T> BlockingCloseableIterator<T> getResultAsync(Class<T> classObject)
  {
    final CDOQueryInfoImpl queryInfo = createQueryInfo();
    final CDOQueryResultIteratorImpl<T> queryResult = new CDOQueryResultIteratorImpl<T>(view, queryInfo);
    final Exception exception[] = new Exception[1];
    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try
        {
          CDOSession session = view.getSession();
          IChannel channel = session.getChannel();
          QueryRequest request = new QueryRequest(channel, view.getViewID(), queryInfo, queryResult);
          session.getFailOverStrategy().send(request);
        }
        catch (Exception ex)
        {
          queryResult.close();
          exception[0] = ex;
        }
      }
    };

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
    if (object instanceof EClass)
    {
      EClass eClass = (EClass)object;
      return ModelUtil.getCDOClass(eClass, view.getSession().getPackageManager());
    }
    else if (object instanceof InternalCDOObject)
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
