/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryQueue;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.AbstractQueryIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Simon McDuff
 */
public class QueryRequest extends CDOClientRequest<List<Object>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryRequest.class);

  private int viewID;

  private AbstractQueryIterator<?> queryResult;

  public QueryRequest(CDOClientProtocol protocol, int viewID, AbstractQueryIterator<?> queryResult)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY);
    this.viewID = viewID;
    this.queryResult = queryResult;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
    // TODO Simon: Move I/O logic to CDODataInput/OutputStream?!
    ((CDOQueryInfoImpl)queryResult.getQueryInfo()).write(out);
  }

  @Override
  protected List<Object> confirming(CDODataInput in) throws IOException
  {
    int queryID = in.readInt();
    queryResult.setQueryID(queryID);

    CDOQueryQueue<Object> resultQueue = queryResult.getQueue();
    List<Object> result = new ArrayList<Object>();

    try
    {
      int numberOfObjectsReceived = 0;
      while (in.readBoolean())
      {
        // result
        Object element = in.readCDORevisionOrPrimitive();
        resultQueue.add(element);
        numberOfObjectsReceived++;
      }

      if (TRACER.isEnabled())
      {
        TRACER.format("Query executed [{0} elements received]", numberOfObjectsReceived);
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
