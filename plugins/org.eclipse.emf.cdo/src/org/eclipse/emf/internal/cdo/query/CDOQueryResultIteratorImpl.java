/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.ConcurrentValue;
import org.eclipse.emf.cdo.internal.common.query.AbstractQueryResult;

import org.eclipse.emf.internal.cdo.protocol.QueryCancelRequest;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends AbstractQueryResult<T>
{
  private static final int QUERYID_UNDEFINED = -1;

  protected boolean closed = false;

  private ConcurrentValue<Boolean> queryIDSet = new ConcurrentValue<Boolean>(false);

  public CDOQueryResultIteratorImpl(CDOView cdoView, CDOQueryInfo queryInfo)
  {
    super(cdoView, queryInfo, QUERYID_UNDEFINED);
  }

  @Override
  public void setQueryID(int queryID)
  {
    super.setQueryID(queryID);
    queryIDSet.set(true);
  }

  public void waitInitialize()
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

  public CDOView getView()
  {
    return (CDOView)super.getView();
  }

  public T next()
  {
    return adapt(super.next());
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  protected T adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      if (((CDOID)object).isNull()) return null;
      return (T)getView().getObject((CDOID)object, true);
    }
    return (T)object;
  }

  public void close()
  {
    if (!isClosed())
    {
      super.close();

      queryIDSet.reevaluate();

      try
      {
        QueryCancelRequest request = new QueryCancelRequest(this.getQueryID(), getView().getSession().getChannel());

        getView().getSession().getFailOverStrategy().send(request);
      }
      catch (Exception exception)
      {
        // Catch all exception
      }
    }
  }
}
