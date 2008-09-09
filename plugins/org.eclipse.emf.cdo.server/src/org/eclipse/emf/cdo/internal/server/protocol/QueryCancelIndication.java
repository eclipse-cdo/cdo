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
import org.eclipse.emf.cdo.internal.server.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelIndication extends CDOReadIndication
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryCancelIndication.class);

  private int queryID;

  public QueryCancelIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY_CANCEL;
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    queryID = in.readInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Query " + queryID + " will be cancelled");
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      getRepository().getQueryManager().cancel(queryID);
      out.writeBoolean(false);
    }
    catch (Exception exception)
    {
      out.writeBoolean(true);
      out.writeString(exception.getMessage());
    }
  }
}
