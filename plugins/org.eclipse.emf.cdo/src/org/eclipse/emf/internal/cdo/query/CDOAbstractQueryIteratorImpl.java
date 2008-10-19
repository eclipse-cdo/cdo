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
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.query.AbstractQueryResult;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.protocol.QueryCancelRequest;

import org.eclipse.net4j.util.concurrent.ConcurrentValue;

import java.util.List;

/**
 * @author Simon McDuff
 */
public abstract class CDOAbstractQueryIteratorImpl<T> extends AbstractQueryResult<T>
{
  private static final int UNDEFINED_QUERY_ID = -1;

  private ConcurrentValue<Boolean> queryIDSet = new ConcurrentValue<Boolean>(false);

  public CDOAbstractQueryIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo, UNDEFINED_QUERY_ID);
  }

  @Override
  public void setQueryID(int queryID)
  {
    super.setQueryID(queryID);
    queryIDSet.set(true);
  }

  public void waitForInitialization() throws InterruptedException
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
  public void remove()
  {
    throw new UnsupportedOperationException();
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
        CDOSessionImpl session = (CDOSessionImpl)getView().getSession();
        QueryCancelRequest request = new QueryCancelRequest(session.getProtocol(), getQueryID());
        session.getFailOverStrategy().send(request);
      }
      catch (Exception ignore)
      {
        // Catch all exception
      }
    }
  }

  public abstract List<T> asList();
}
