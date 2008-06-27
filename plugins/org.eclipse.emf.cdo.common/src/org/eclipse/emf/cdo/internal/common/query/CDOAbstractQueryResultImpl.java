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
package org.eclipse.emf.cdo.internal.common.query;

import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.query.ResultReaderQueue;
import org.eclipse.emf.cdo.common.query.ResultWriterQueue;
import org.eclipse.emf.cdo.common.util.PollIterator;
import org.eclipse.emf.cdo.common.util.StateConcurrentQueue;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author Simon McDuff
 */
public abstract class  CDOAbstractQueryResultImpl<T> extends Lifecycle implements ResultReaderQueue<T>
{
  protected long queryID = -1;

  protected CDOQueryParameter cdoQueryParameter;

  protected StateConcurrentQueue<Object> queue = new StateConcurrentQueue<Object>();

  protected Iterator<Object> nextObject = new PollIterator<Object>(queue);
  
  public CDOAbstractQueryResultImpl(CDOQueryParameter cdoQueryParameter)
  {
    this.cdoQueryParameter = cdoQueryParameter;
  }
  
  public ResultWriterQueue<Object> getResultQueue()
  {
    return queue;
  }
  
  public void setQueryID(long queryID)
  {
    this.queryID = queryID;
  }

  public boolean isDone()
  {
    return queue.isDone();
  }

  public long getQueryID()
  {
    return queryID;
  }

  public List<T> getAsList()
  {
    ArrayList<T> result = new ArrayList<T>();
    while (hasNext())
    {
      result.add(next());
    }
    return result;
  }

}
