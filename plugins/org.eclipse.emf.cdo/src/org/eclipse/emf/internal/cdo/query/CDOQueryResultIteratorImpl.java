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
import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.util.PropertyChanged;
import org.eclipse.emf.cdo.internal.common.query.CDOAbstractQueryResultImpl;
import org.eclipse.emf.cdo.query.CDOQueryResult;

import org.eclipse.emf.internal.cdo.protocol.QueryCancelRequest;

import org.eclipse.net4j.util.lifecycle.ILifecycleState;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends CDOAbstractQueryResultImpl<T> implements CDOQueryResult<T>
{
  protected CDOView cdoView;

  protected boolean cancelled = false;

  protected PropertyChanged<ILifecycleState> state = new PropertyChanged<ILifecycleState>(getLifecycleState());

  public CDOQueryResultIteratorImpl(CDOView cdoView, CDOQueryParameter cdoQueryParameter)
  {
    super(cdoQueryParameter);
    this.cdoView = cdoView;
  }

  public void waitForActivate()
  {
    state.acquire(ILifecycleState.ACTIVE, null);
  }

  @Override
  protected void doActivate() throws Exception
  {
    state.set(ILifecycleState.ACTIVE);
    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    state.set(ILifecycleState.INACTIVE);
    super.doDeactivate();
  }

  public boolean hasNext()
  {
    return nextObject.hasNext();
  }

  public T next()
  {
    return adapt(nextObject.next());
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
      return (T)cdoView.getObject((CDOID)object, true);
    }
    return (T)object;
  }

  public void close()
  {
    cancelled = true;
    try
    {
      new QueryCancelRequest(queryID, cdoView.getSession().getChannel()).send();
    }
    catch (Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public boolean cancel(boolean arg0)
  {
    close();
    return true;
  }

  public T get() throws InterruptedException, ExecutionException
  {
    return null;
  }

  public T get(long arg0, TimeUnit arg1) throws InterruptedException, ExecutionException, TimeoutException
  {
    return null;
  }

  public boolean isCancelled()
  {
    return cancelled;
  }

}
