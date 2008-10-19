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
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryInfoImpl;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.query.CDOAbstractQueryIteratorImpl;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class QueryRequest extends CDOClientRequest<Object>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRequest.class);

  private int viewID;

  private CDOAbstractQueryIteratorImpl<?> queryResult;

  public QueryRequest(CDOClientProtocol protocol, int viewID, CDOAbstractQueryIteratorImpl<?> queryResult)
  {
    super(protocol);
    this.viewID = viewID;
    this.queryResult = queryResult;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    // TODO Simon: Move I/O logic to CDODataInput/OutputStream?!
    ((CDOQueryInfoImpl)queryResult.getQueryInfo()).write(out);
  }

  @Override
  protected Object confirming(CDODataInput in) throws IOException
  {
    int queryID = in.readInt();
    queryResult.setQueryID(queryID);

    CDOQueryQueue<Object> resultQueue = queryResult.getQueue();
    List<Object> result = new ArrayList<Object>();

    try
    {
      int numberOfObjectsReceived = 0;
      while (true)
      {
        byte state = in.readByte();
        if (state == CDOProtocolConstants.QUERY_MORE_OBJECT)
        {
          // result
          Object element = in.readCDORevisionOrPrimitive();
          resultQueue.add(element);
          numberOfObjectsReceived++;
        }
        else if (state == CDOProtocolConstants.QUERY_DONE)
        {
          // End of result
          break;
        }
        else if (state == CDOProtocolConstants.QUERY_EXCEPTION)
        {
          // Exception on the server
          String exceptionString = in.readString();
          throw new RuntimeException(exceptionString);
        }
        else
        {
          throw new IllegalStateException();
        }
      }

      if (PROTOCOL_TRACER.isEnabled())
      {
        PROTOCOL_TRACER.format("Query executed [{0} elements received]", numberOfObjectsReceived);
      }
    }
    catch (RuntimeException ex)
    {
      resultQueue.setException(ex);
    }
    catch (Throwable throwable)
    {
      resultQueue.setException(new RuntimeException(throwable.getMessage(), throwable));
    }
    finally
    {
      resultQueue.close();
    }

    return result;
  }
}
