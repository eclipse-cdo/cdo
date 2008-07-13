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
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryParameterImpl;
import org.eclipse.emf.cdo.query.CDOQuery;
import org.eclipse.emf.cdo.query.CDOQueryResult;

import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.protocol.QueryRequest;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author Simon McDuff
 */
public class CDOQueryImpl implements CDOQuery
{
  private CDOQueryParameterImpl queryParameter;

  private HashMap<String, Object> parameters = new HashMap<String, Object>();

  private CDOViewImpl view = null;

  private int maxResult = -1;

  public CDOQueryImpl(CDOViewImpl cdoView, CDOQueryParameterImpl queryParameter)
  {
    view = cdoView;
    this.queryParameter = queryParameter;
  }

  protected CDOQueryParameterImpl createQueryParameter()
  {
    CDOQueryParameterImpl queryParameter = new CDOQueryParameterImpl(this.queryParameter.getQueryLanguage(),
        this.queryParameter.getQueryString());

    queryParameter.setMaxResult(maxResult);

    for (Entry<String, Object> entry : parameters.entrySet())
    {
      Object value = entry.getValue();

      value = adapt(value);

      queryParameter.getParameters().put(entry.getKey(), value);
    }
    return queryParameter;
  }

  public <T> List<T> getResultList(Class<T> classObject) throws Exception
  {
    CDOQueryParameterImpl queryParameter = createQueryParameter();

    CDOQueryResultIteratorImpl<T> queryResult = new CDOQueryResultIteratorImpl<T>(view, queryParameter);

    new QueryRequest(view.getViewID(), view.getSession().getChannel(), queryResult, queryParameter).send();

    return queryResult.getAsList();
  }

  public <T> CDOQueryResult<T> getResultIterator(Class<T> classObject)
  {
    final CDOQueryParameterImpl queryParameter = createQueryParameter();
    final CDOQueryResultIteratorImpl<T> queryResult = new CDOQueryResultIteratorImpl<T>(view, queryParameter);

    Runnable runnable = new Runnable()
    {
      public void run()
      {
        try
        {

          new QueryRequest(view.getViewID(), view.getSession().getChannel(), queryResult, queryParameter).send();
        }
        catch (RuntimeException ex)
        {
          queryResult.getResultQueue().setException(ex);

          // Be sure we activate queryResultObject
          if (!queryResult.isActive()) LifecycleUtil.activate(queryResult);
        }
        catch (Exception ex)
        {
          queryResult.getResultQueue().setException(new RuntimeException(ex));

          // Be sure we activate queryResult
          if (!queryResult.isActive()) LifecycleUtil.activate(queryResult);
        }
      }
    };

    new Thread(runnable).start();

    queryResult.waitForActivate();

    return queryResult;
  }

  protected Object adapt(Object object)
  {
    if (object instanceof EClass)
    {
      EClass eClass = (EClass)object;
      CDOClass cdoClass = ModelUtil.getCDOClass(eClass, view.getSession().getPackageManager());
      return cdoClass;
    }
    else if (object instanceof InternalCDOObject)
    {
      InternalCDOObject internalCDOObject = FSMUtil.adapt(object, view);

      if (internalCDOObject.cdoID() == null) throw new UnsupportedOperationException("Object not persisted");

      if (internalCDOObject.cdoID().isTemporary()) throw new UnsupportedOperationException("Object not persisted");

      return internalCDOObject.cdoID();
    }

    return object;
  }

  public CDOQuery setMaxResults(int maxResult)
  {
    this.maxResult = maxResult;
    return this;
  }

  public int getMaxResults()
  {
    return maxResult;
  }

  public CDOQuery setParameter(String name, Object value)
  {
    parameters.put(name, value);
    return this;
  }

}
