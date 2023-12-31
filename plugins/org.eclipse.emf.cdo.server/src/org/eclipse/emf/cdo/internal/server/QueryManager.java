/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryManager extends Lifecycle implements InternalQueryManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, QueryManager.class);

  private InternalRepository repository;

  private Map<Integer, QueryContext> queryContexts = new ConcurrentHashMap<>();

  private ExecutorService executors;

  private boolean shutdownExecutorService;

  private int nextQuery;

  private boolean allowInterruptRunningQueries = true;

  public QueryManager()
  {
  }

  @Override
  public InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  public void setRepository(InternalRepository repository)
  {
    this.repository = repository;

    String value = repository.getProperties().get(IRepository.Props.ALLOW_INTERRUPT_RUNNING_QUERIES);
    if (value != null)
    {
      allowInterruptRunningQueries = Boolean.parseBoolean(value);
    }
  }

  public synchronized ExecutorService getExecutors()
  {
    if (executors == null)
    {
      executors = ConcurrencyUtil.getExecutorService(repository);

      if (executors == null)
      {
        shutdownExecutorService = true;
        executors = ThreadPool.create();
      }
    }

    return executors;
  }

  public synchronized void setExecutors(ExecutorService executors)
  {
    if (shutdownExecutorService)
    {
      if (this.executors != null)
      {
        this.executors.shutdown();
      }

      shutdownExecutorService = false;
    }

    this.executors = executors;
  }

  @Override
  public InternalQueryResult execute(InternalView view, CDOQueryInfo queryInfo)
  {
    IQueryHandler handler = repository.getQueryHandler(queryInfo);

    InternalQueryResult queryResult = new QueryResult(view, queryInfo, getNextQueryID(), handler);
    QueryContext queryContext = new QueryContext(queryResult);
    execute(queryContext);
    return queryResult;
  }

  @Override
  public boolean isRunning(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    return queryContext != null;
  }

  @Override
  public void cancel(int queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);
    if (queryContext == null || queryContext.getFuture().isDone())
    {
      throw new RuntimeException("Query " + queryID + " is not running anymore"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Cancelling query for context: " + queryContext); //$NON-NLS-1$
    }

    queryContext.cancel();
  }

  public synchronized void register(QueryContext queryContext)
  {
    int queryID = queryContext.getQueryResult().getQueryID();
    queryContexts.put(queryID, queryContext);
    queryContext.addListener();
  }

  public synchronized void unregister(QueryContext queryContext)
  {
    int queryID = queryContext.getQueryResult().getQueryID();
    queryContexts.remove(queryID);
    queryContext.removeListener();
  }

  public synchronized int getNextQueryID()
  {
    return ++nextQuery;
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();
    setExecutors(null);
  }

  private Future<?> execute(QueryContext queryContext)
  {
    register(queryContext);

    InternalSession session = queryContext.getView().getSession();

    ExecutorService executors = getExecutors();
    Future<?> future = executors.submit(StoreThreadLocal.wrap(session, queryContext));

    queryContext.setFuture(future);
    return future;
  }

  /**
   * @author Simon McDuff
   * @since 2.0
   */
  private final class QueryContext implements IQueryContext, Runnable
  {
    private final IListener sessionListener = new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof SingleDeltaContainerEvent<?>)
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

    private CDOBranchPoint branchPoint;

    private InternalQueryResult queryResult;

    private boolean started;

    private volatile boolean cancelled;

    private int resultCount;

    private Future<?> future;

    public QueryContext(InternalQueryResult queryResult)
    {
      this.queryResult = queryResult;

      // Remember the branchPoint because it can change
      InternalView view = getView();
      branchPoint = CDOBranchUtil.copyBranchPoint(view);
    }

    public InternalQueryResult getQueryResult()
    {
      return queryResult;
    }

    @Override
    public InternalView getView()
    {
      return queryResult.getView();
    }

    @Override
    public CDOBranch getBranch()
    {
      return branchPoint.getBranch();
    }

    @Override
    public long getTimeStamp()
    {
      return branchPoint.getTimeStamp();
    }

    public Future<?> getFuture()
    {
      return future;
    }

    public void setFuture(Future<?> future)
    {
      this.future = future;
    }

    @Override
    public boolean isCancelled()
    {
      return cancelled;
    }

    public void cancel()
    {
      if (!cancelled)
      {
        cancelled = true;

        if (future != null)
        {
          future.cancel(allowInterruptRunningQueries);
        }

        if (!started)
        {
          unregister(this);
        }
      }
    }

    @Override
    public int getResultCount()
    {
      return resultCount;
    }

    @Override
    public boolean addResult(Object object)
    {
      if (resultCount == 0)
      {
        throw new IllegalStateException("Maximum number of results exceeded"); //$NON-NLS-1$
      }

      CDOQueryQueue<Object> queue = queryResult.getQueue();
      queue.add(object);

      return !cancelled && --resultCount > 0;
    }

    @Override
    public void run()
    {
      CDOQueryQueue<Object> queue = queryResult.getQueue();

      try
      {
        started = true;

        CDOQueryInfo info = queryResult.getQueryInfo();
        resultCount = info.getMaxResults() < 0 ? Integer.MAX_VALUE : info.getMaxResults();

        try
        {
          IQueryHandler handler = queryResult.getQueryHandler();
          handler.executeQuery(info, this);
        }
        catch (Throwable executionException)
        {
          addResult(executionException);
          return;
        }
      }
      catch (Throwable initializationException)
      {
        queue.setException(initializationException);
      }
      finally
      {
        queue.close();
        unregister(this);
      }
    }

    public void addListener()
    {
      InternalSession session = getQueryResult().getView().getSession();
      session.addListener(sessionListener);
    }

    public void removeListener()
    {
      InternalSession session = getQueryResult().getView().getSession();
      session.removeListener(sessionListener);
    }
  }
}
