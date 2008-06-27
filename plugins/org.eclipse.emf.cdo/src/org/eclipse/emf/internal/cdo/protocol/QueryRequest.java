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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.query.ResultWriterQueue;
import org.eclipse.emf.cdo.common.util.CDOInstanceUtil;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryParameterImpl;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.query.CDOQueryResultIteratorImpl;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class QueryRequest extends CDOClientRequest<Object>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRequest.class);

  private int viewID;

  private CDOQueryParameterImpl cdoQuery;

  private CDOQueryResultIteratorImpl<?> queryResult;

  public QueryRequest(int viewID, IChannel channel, CDOQueryResultIteratorImpl<?> queryResult,
      CDOQueryParameterImpl cdoQuery)
  {
    super(channel);

    this.viewID = viewID;
    this.cdoQuery = cdoQuery;
    this.queryResult = queryResult;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    // Write ViewID
    out.writeInt(viewID);

    cdoQuery.write(out);

  }
  
  @Override
  protected List<Object> confirming(ExtendedDataInputStream in) throws IOException
  {

    List<Object> returnList = new ArrayList<Object>();
    
    queryResult.setQueryID(in.readLong());
    
    LifecycleUtil.activate(queryResult);
    
    ResultWriterQueue<Object> resulQueue = queryResult.getResultQueue();
    
    try
    {
      int numberOfObjectReceived = 0;
      
      while (true)
      {
        byte state = in.readByte();

        if (state == 0)
        {
          // result
          Object element = CDOInstanceUtil.readInstance(in, getSession());

          resulQueue.add(element);
          
          numberOfObjectReceived++;
        }
        else if (state == 1)
        {
          // End of result
          break;
        }
        else if (state == 2)
        {
          // Exception on the server
          String exceptionString = in.readString();
          throw new RuntimeException(exceptionString);
        }
      }
      
      if (PROTOCOL.isEnabled())
      {
        PROTOCOL.trace("Query executed [" + numberOfObjectReceived + " elements received]");
      }
    }
    catch (RuntimeException ex)
    {
      resulQueue.setException(ex);
      
    }
    catch (Throwable throwable)
    {
      resulQueue.setException(new RuntimeException(throwable.getMessage(), throwable));
    }
    finally
    {
      resulQueue.release();
    }
    
    return returnList;
  }
}
