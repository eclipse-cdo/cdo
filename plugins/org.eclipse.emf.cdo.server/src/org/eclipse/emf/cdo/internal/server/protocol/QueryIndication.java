/***************************************************************************
 * Copyright (c) 2004-2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.server.protocol;

import org.eclipse.emf.cdo.common.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.query.CDOQueryParameter;
import org.eclipse.emf.cdo.common.util.CDOInstanceUtil;
import org.eclipse.emf.cdo.internal.common.query.CDOQueryParameterImpl;
import org.eclipse.emf.cdo.internal.server.QueryManager;
import org.eclipse.emf.cdo.internal.server.QueryResult;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.IView;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryIndication.class);

  private QueryResult queryResult = null;

  public QueryIndication()
  {
    super();
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int viewID = in.readInt();

    CDOQueryParameter cdoQuery = new CDOQueryParameterImpl(in, getStore().getCDOIDObjectFactory(), getPackageManager());

    IView view = getSession().getView(viewID);

    QueryManager queryManager = getRepository().getQueryManager();
    
    queryResult = queryManager.execute(view, cdoQuery);
  }

  /**
   * @see org.eclipse.net4j.signal.IndicationWithResponse#responding(org.eclipse.net4j.util.io.ExtendedDataOutputStream)
   */
  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    try
    {
      int numberOfResult = 0;

      // Return queryID immediately.
      out.writeLong(queryResult.getQueryID());
      out.flush();

      while (queryResult.hasNext())
      {
        Object object = queryResult.next();

        // Object to return
        numberOfResult++;
        out.writeByte(0);
        CDOInstanceUtil.writeInstance(out, object);

        if (!queryResult.hasNextNow())
        {
          // Flush only if empty
          out.flush();
        }
      }
      if (TRACER.isEnabled())
      {
        TRACER.trace("Query had " + numberOfResult + " objects return");
      }

      // DONE
      out.writeByte(1);
    }
    catch (Exception exception)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(exception);
      }

      // Exception occured
      out.writeByte(2);
      out.writeString(exception.getMessage());
    }
  }
}
