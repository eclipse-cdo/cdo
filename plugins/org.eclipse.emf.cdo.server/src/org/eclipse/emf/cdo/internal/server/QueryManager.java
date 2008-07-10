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
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.query.ResultWriterQueue;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IStoreReader;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreUtil;

import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
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
public class QueryManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SESSION, QueryManager.class);

  private Map<Long, QueryContext> queryContexts = new ConcurrentHashMap<Long, QueryContext>();

  private Map<Long, Future<?>> associateThreads = new ConcurrentHashMap<Long, Future<?>>();

  private ExecutorService executors = Executors.newFixedThreadPool(10);

  private long nextQuery = 0;

  public QueryManager()
  {
  }

  synchronized long nextQuery()
  {
    return nextQuery++;
  }

  private Future<?> execute(QueryContext queryContext)
  {
    Future<?> future = executors.submit(queryContext);

    registerQueryNative(queryContext, future);

    return future;
  }

  class QueryContext implements Runnable
  {
    QueryResult queryResult;

    IListener listener = null;

    IStoreReader reader = null;

    public QueryContext(IStoreReader reader, QueryResult queryResult)
    {
      this.queryResult = queryResult;
      this.reader = reader;
    }

    private void removeListener()
    {
      final IView view = (IView)getQueryResult().getView();
      view.getSession().removeListener(listener);
      listener = null;
    }

    private void addListener()
    {
      if (listener == null)
      {
        final IView view = (IView)getQueryResult().getView();

        listener = new IListener()
        {
          public void notifyEvent(IEvent event)
          {
            if (event instanceof SingleDeltaContainerEvent)
            {
              SingleDeltaContainerEvent<?> deltaEvent = (SingleDeltaContainerEvent<?>)event;
              if (deltaEvent.getDeltaKind() == Kind.REMOVED && deltaEvent.getDeltaElement() == view)
              {
                // Cancel the query when view is closing
                cancel(getQueryResult().getQueryID());
              }
            }
          }
        };

        // Add listener to the session
        view.getSession().addListener(listener);
      }
    }

    public void run()
    {
      ResultWriterQueue<Object> resultQueue = queryResult.getResultWriterQueue();

      CloseableIterator<Object> itrResult = null;

      try
      {
        itrResult = reader.createQueryIterator(queryResult.getQueryParameter());

        int maxResult = queryResult.getQueryParameter().getMaxResult();

        if (maxResult < 0) maxResult = Integer.MAX_VALUE;

        for (int i = 0; i < maxResult && itrResult.hasNext(); i++)
        {
          resultQueue.add(itrResult.next());
        }
      }
      catch (RuntimeException exception)
      {
        resultQueue.setException(exception);
      }
      catch (Throwable exception)
      {
        resultQueue.setException(new RuntimeException(exception));
      }
      finally
      {
        resultQueue.release();

        if (itrResult != null) itrResult.close();

        unregisterQueryNative(this);

      }
    }

    public QueryResult getQueryResult()
    {
      return queryResult;
    }
  }

  public QueryResult execute(IView view, CDOQueryParameter queryParameter)
  {

    QueryResult queryResult = new QueryResult(view, queryParameter, nextQuery());

    QueryContext queryContext = new QueryContext(StoreUtil.getReader(), queryResult);

    execute(queryContext);

    return queryResult;
  }

  public boolean isRunning(long queryID)
  {
    QueryContext queryContext = queryContexts.get(queryID);

    return queryContext != null;
  }

  public void cancel(long queryID)
  {
    Future<?> queryContext = associateThreads.get(queryID);

    if (queryContext == null || queryContext.isDone())
      throw new RuntimeException("Query " + queryID + " is not running anymore");

    if (TRACER.isEnabled()) TRACER.trace("Stopping QueryNative thread: " + queryContext);

    queryContext.cancel(true);
  }

  synchronized public void registerQueryNative(final QueryContext queryContext, Future<?> future)
  {
    queryContexts.put(queryContext.getQueryResult().getQueryID(), queryContext);
    associateThreads.put(queryContext.getQueryResult().getQueryID(), future);
    queryContext.addListener();
  }

  synchronized public void unregisterQueryNative(final QueryContext queryContext)
  {
    queryContexts.remove(queryContext.getQueryResult().getQueryID());
    associateThreads.remove(queryContext.getQueryResult().getQueryID());
    queryContext.removeListener();
  }
}
