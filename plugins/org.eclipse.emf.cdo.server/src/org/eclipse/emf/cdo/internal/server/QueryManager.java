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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryManager extends Lifecycle
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, QueryManager.class);

  private Map<Integer, QueryContext> queryContexts = new ConcurrentHashMap<Integer, QueryContext>();

  private ExecutorService executors;

  private int nextQuery;

  public QueryManager()
  {
  }

  public synchronized ExecutorService getExecutors()
  {
    if (executors == null)
    {
      executors = Executors.newFixedThreadPool(10);
    }

    return executors;
  }

  public void setExecutors(ExecutorService executors)
  {
    this.executors = executors;
  }

  public QueryResult execute(IView view, CDOQueryInfo queryInfo)
  {
    QueryResult queryResult = new QueryResult(view, queryInfo, nextQuery());
    QueryContext queryContext = new QueryContext(StoreThreadLocal.getStoreReader(), queryResult);
    execute(queryContext);
    return queryResult;
  }

  public boolean isRunning(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    return queryContext != null;
  }

  public void cancel(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    if (queryContext == null || queryContext.getFuture().isDone())
    {
      throw new RuntimeException("Query " + queryID + " is not running anymore");
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Stopping QueryNative thread: " + queryContext);
    }

    queryContext.getFuture().cancel(true);
  }

  public synchronized void register(final QueryContext queryContext)
  {
    queryContexts.put(queryContext.getQueryResult().getQueryID(), queryContext);
    queryContext.addListener();
  }

  public synchronized void unregister(final QueryContext queryContext)
  {
    queryContexts.remove(queryContext.getQueryResult().getQueryID());
    queryContext.removeListener();
  }

  public synchronized int nextQuery()
  {
    return nextQuery++;
  }

  private Future<?> execute(QueryContext queryContext)
  {
    Future<?> future = getExecutors().submit(queryContext);
    queryContext.setFuture(future);
    register(queryContext);
    return future;
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private class QueryContext implements Runnable
  {
    private QueryResult queryResult;

    private Future<?> future;

    private IStoreReader reader;

    private IListener listener = new IListener()
    {
      public void notifyEvent(IEvent event)
      {
        if (event instanceof SingleDeltaContainerEvent)
        {
          IView view = getQueryResult().getView();
          SingleDeltaContainerEvent<?> deltaEvent = (SingleDeltaContainerEvent<?>)event;
          if (deltaEvent.getDeltaKind() == Kind.REMOVED && deltaEvent.getDeltaElement() == view)
          {
            // Cancel the query when view is closing
            cancel(getQueryResult().getQueryID());
          }
        }
      }
    };

    public QueryContext(IStoreReader reader, QueryResult queryResult)
    {
      this.queryResult = queryResult;
      this.reader = reader;
    }

    public void run()
    {
      CDOQueryQueue<Object> resultQueue = queryResult.getQueue();
      CloseableIterator<Object> itrResult = null;

      try
      {
        itrResult = reader.createQueryIterator(queryResult.getQueryInfo());
        int maxResult = queryResult.getQueryInfo().getMaxResults();
        if (maxResult < 0)
        {
          maxResult = Integer.MAX_VALUE;
        }

        for (int i = 0; i < maxResult && itrResult.hasNext(); i++)
        {
          resultQueue.add(itrResult.next());
        }
      }
      catch (Throwable exception)
      {
        resultQueue.setException(exception);
      }
      finally
      {
        resultQueue.close();
        if (itrResult != null)
        {
          itrResult.close();
        }

        unregister(this);
      }
    }

    public QueryResult getQueryResult()
    {
      return queryResult;
    }

    protected Future<?> getFuture()
    {
      return future;
    }

    protected void setFuture(Future<?> future)
    {
      this.future = future;
    }

    private void removeListener()
    {
      IView view = getQueryResult().getView();
      view.getSession().removeListener(listener);
    }

    private void addListener()
    {
      final IView view = getQueryResult().getView();
      view.getSession().addListener(listener);
    }
  }
}
