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
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepositoryElement;
import org.eclipse.emf.cdo.server.IView;

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
public class QueryManager extends Lifecycle implements IRepositoryElement
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, QueryManager.class);

  private Repository repository;

  private Map<Integer, QueryContext> queryContexts = new ConcurrentHashMap<Integer, QueryContext>();

  private ExecutorService executors;

  private int nextQuery;

  public QueryManager(Repository repository)
  {
    this.repository = repository;
  }

  public Repository getRepository()
  {
    return repository;
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
    QueryContext queryContext = new QueryContext(queryResult);
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
      TRACER.trace("Cancelling query for context: " + queryContext);
    }

    queryContext.cancel();
  }

  public synchronized void register(final QueryContext queryContext)
  {
    queryContexts.put(queryContext.getQueryResult().getQueryID(), queryContext);
    queryContext.addListener();
  }

  public synchronized void unregister(final QueryContext queryContext)
  {
    if (queryContexts.remove(queryContext.getQueryResult().getQueryID()) != null)
    {
      queryContext.removeListener();
    }
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
  private class QueryContext implements IQueryContext, Runnable
  {
    private QueryResult queryResult;

    private boolean started;

    private boolean cancelled;

    private int resultCount;

    private Future<?> future;

    private IListener sessionListener = new IListener()
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
            cancel();
          }
        }
      }
    };

    public QueryContext(QueryResult queryResult)
    {
      this.queryResult = queryResult;
    }

    public QueryResult getQueryResult()
    {
      return queryResult;
    }

    public IView getView()
    {
      return queryResult.getView();
    }

    public Future<?> getFuture()
    {
      return future;
    }

    public void setFuture(Future<?> future)
    {
      this.future = future;
    }

    public void cancel()
    {
      cancelled = true;
      if (future != null)
      {
        future.cancel(true);
      }

      if (!started)
      {
        unregister(this);
      }
    }

    public boolean addResult(Object object)
    {
      if (resultCount == 0)
      {
        throw new IllegalStateException("Maximum number of results exceeded");
      }

      queryResult.getQueue().add(object);
      return !cancelled && --resultCount > 0;
    }

    public void run()
    {
      try
      {
        started = true;
        CDOQueryInfo info = queryResult.getQueryInfo();
        resultCount = info.getMaxResults() < 0 ? Integer.MAX_VALUE : info.getMaxResults();
        IQueryHandler handler = repository.getQueryHandler(info);
        handler.executeQuery(info, this);
      }
      catch (Throwable exception)
      {
        queryResult.getQueue().setException(exception);
      }
      finally
      {
        queryResult.getQueue().close();
        unregister(this);
      }
    }

    public void addListener()
    {
      IView view = getQueryResult().getView();
      view.getSession().addListener(sessionListener);
    }

    public void removeListener()
    {
      IView view = getQueryResult().getView();
      view.getSession().removeListener(sessionListener);
    }
  }
}
