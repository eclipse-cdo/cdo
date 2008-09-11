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
package org.eclipse.emf.cdo.internal.common.query;

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.common.util.BlockingCloseableIterator;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class AbstractQueryResult<T> implements BlockingCloseableIterator<T>
{
  private int queryID;

  private CDOProtocolView view;

  private CDOQueryInfo queryInfo;

  private CDOQueryQueue<Object> linkQueue = new CDOQueryQueue<Object>();

  private BlockingCloseableIterator<Object> queueItr = linkQueue.iterator();

  public AbstractQueryResult(CDOProtocolView view, CDOQueryInfo queryInfo, int queryID)
  {
    this.queryID = queryID;
    this.view = view;
    this.queryInfo = queryInfo;
  }

  public CDOQueryInfo getQueryInfo()
  {
    return queryInfo;
  }

  public CDOQueryQueue<Object> getQueue()
  {
    return linkQueue;
  }

  public CDOProtocolView getView()
  {
    return view;
  }

  public int getQueryID()
  {
    return queryID;
  }

  public void setQueryID(int queryID)
  {
    this.queryID = queryID;
  }

  @SuppressWarnings("unchecked")
  public T peek()
  {
    return (T)queueItr.peek();
  }

  public boolean hasNext()
  {
    return queueItr.hasNext();
  }

  @SuppressWarnings("unchecked")
  public T next()
  {
    return (T)queueItr.next();
  }

  public void remove()
  {
    throw new UnsupportedOperationException();
  }

  public void close()
  {
    queueItr.close();
  }

  public boolean isClosed()
  {
    return queueItr.isClosed();
  }
}
