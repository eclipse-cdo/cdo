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
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer PROTOCOL_TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, QueryCancelRequest.class);

  private int queryID;

  public QueryCancelRequest(CDOClientProtocol protocol, int queryID)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_QUERY_CANCEL);
    this.queryID = queryID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (PROTOCOL_TRACER.isEnabled())
    {
      PROTOCOL_TRACER.trace("Cancel query " + queryID);
    }

    out.writeInt(queryID);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    boolean exception = in.readBoolean();
    if (exception)
    {
      String message = in.readString();
      throw new RuntimeException(message);
    }

    return true;
  }
}
