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

import org.eclipse.emf.cdo.common.CDOProtocolView;
import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.query.ResultWriterQueue;
import org.eclipse.emf.cdo.common.util.PollIterator;
import org.eclipse.emf.cdo.common.util.StateConcurrentQueue;
import org.eclipse.emf.cdo.server.IView;

import java.util.Iterator;

/**
 * @author Simon McDuff
 */
public class QueryResult
{
  private long queryID;

  private CDOProtocolView view;

  private CDOQueryParameter queryParameter;

  private StateConcurrentQueue<Object> linkQueue = new StateConcurrentQueue<Object>();

  protected Iterator<Object> nextObject = new PollIterator<Object>(linkQueue);

  public QueryResult(IView view, CDOQueryParameter parameter, long queryID)
  {
    this.queryID = queryID;
    this.view = view;
    this.queryParameter = parameter;
  }

  public CDOQueryParameter getQueryParameter()
  {
    return queryParameter;
  }

  public ResultWriterQueue<Object> getResultWriterQueue()
  {
    return linkQueue;
  }

  public CDOProtocolView getView()
  {
    return view;
  }
  
  public long getQueryID()
  {
    return queryID;
  }

  public boolean hasNext()
  {
    return nextObject.hasNext();
  }

  public Object next()
  {
    return nextObject.next();
  }

  public boolean hasNextNow()
  {
    return !linkQueue.isEmpty();
  }

}
