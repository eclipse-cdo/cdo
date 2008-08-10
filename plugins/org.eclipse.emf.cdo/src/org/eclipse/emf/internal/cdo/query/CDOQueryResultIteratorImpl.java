/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.ConcurrentValue;
import org.eclipse.emf.cdo.internal.common.query.AbstractQueryResult;

import org.eclipse.emf.internal.cdo.protocol.QueryCancelRequest;

import org.eclipse.net4j.channel.IChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends AbstractQueryResult<T>
{
  private static final int UNDEFINED_QUERY_ID = -1;

  private ConcurrentValue<Boolean> queryIDSet = new ConcurrentValue<Boolean>(false);

  public CDOQueryResultIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo, UNDEFINED_QUERY_ID);
  }

  @Override
  public void setQueryID(int queryID)
  {
    super.setQueryID(queryID);
    queryIDSet.set(true);
  }

  public void waitForInitialization()
  {
    queryIDSet.acquire(new Object()
    {
      @Override
      public boolean equals(Object obj)
      {
        return Boolean.TRUE.equals(obj) || isClosed();
      }
    });
  }

  @Override
  public CDOView getView()
  {
    return (CDOView)super.getView();
  }

  @Override
  public T next()
  {
    return adapt(super.next());
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  protected T adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      if (((CDOID)object).isNull())
      {
        return null;
      }

      return (T)getView().getObject((CDOID)object, true);
    }

    return (T)object;
  }

  @Override
  public void close()
  {
    if (!isClosed())
    {
      super.close();
      queryIDSet.reevaluate();

      try
      {
        CDOSession session = getView().getSession();
        IChannel channel = session.getChannel();
        QueryCancelRequest request = new QueryCancelRequest(channel, getQueryID());
        session.getFailOverStrategy().send(request);
      }
      catch (Exception exception)
      {
        // Catch all exception
      }
    }
  }

  public List<T> getAsList()
  {
    ArrayList<Object> result = new ArrayList<Object>();
    while (super.hasNext())
    {
      result.add(super.next());
    }

    return new CDOEList<T>(getView(), result);
  }
}
