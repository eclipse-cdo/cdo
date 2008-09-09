/***************************************************************************
 * Copyright (c) 2004-2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDODataInput;
import org.eclipse.emf.cdo.common.CDODataOutput;
import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.query.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.internal.server.QueryManager;
import org.eclipse.emf.cdo.internal.server.QueryResult;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryIndication.class);

  private QueryResult queryResult;

  public QueryIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    CDOQueryInfo cdoQuery = new CDOQueryInfoImpl(in); // TODO Add CDODataInput.readCDOQueryInfo()
    IView view = getSession().getView(viewID);
    QueryManager queryManager = getRepository().getQueryManager();
    queryResult = queryManager.execute(view, cdoQuery);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      int numberOfResults = 0;

      // Return queryID immediately.
      out.writeInt(queryResult.getQueryID());
      flush();

      while (queryResult.hasNext())
      {
        Object object = queryResult.next();

        // Object to return
        numberOfResults++;
        out.writeByte(CDOProtocolConstants.QUERY_MORE_OBJECT);
        out.writeCDORevisionOrPrimitive(object);
        if (queryResult.peek() == null)
        {
          flush();
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Query had " + numberOfResults + " objects return");
      }

      // Query is done successfully
      out.writeByte(CDOProtocolConstants.QUERY_DONE);
    }
    catch (Exception exception)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(exception);
      }

      // Exception occured during the query
      out.writeByte(CDOProtocolConstants.QUERY_EXCEPTION);
      out.writeString(exception.getMessage());
    }
  }
}
