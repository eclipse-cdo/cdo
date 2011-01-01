/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.CDOQueryInfoImpl;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EReference;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryIndication.class);

  private boolean xrefs;

  private InternalQueryResult queryResult;

  public QueryIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int viewID = in.readInt();
    InternalView view = getSession().getView(viewID);

    CDOQueryInfo queryInfo = new CDOQueryInfoImpl(in);
    xrefs = queryInfo.getQueryLanguage().equals(CDOProtocolConstants.QUERY_LANGUAGE_XREFS);

    InternalQueryManager queryManager = getRepository().getQueryManager();
    queryResult = queryManager.execute(view, queryInfo);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    // Return queryID immediately.
    out.writeInt(queryResult.getQueryID());
    flush();

    int numberOfResults = 0;
    while (queryResult.hasNext())
    {
      Object object = queryResult.next();

      // Object to return
      numberOfResults++;
      out.writeBoolean(true);

      if (xrefs)
      {
        Object[] values = (Object[])object;
        CDOID targetID = (CDOID)values[0];
        CDOID sourceID = (CDOID)values[1];
        EReference sourceReference = (EReference)values[2];
        int sourceIndex = (Integer)values[3];

        out.writeCDOID(targetID);
        out.writeCDOID(sourceID);
        out.writeCDOClassifierRef(sourceReference.getEContainingClass());
        out.writeString(sourceReference.getName());
        out.writeInt(sourceIndex);
      }
      else
      {
        out.writeCDORevisionOrPrimitive(object);
      }

      if (queryResult.peek() == null)
      {
        flush();
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Query returned " + numberOfResults + " results"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    // Query is done successfully
    out.writeBoolean(false);
  }
}
