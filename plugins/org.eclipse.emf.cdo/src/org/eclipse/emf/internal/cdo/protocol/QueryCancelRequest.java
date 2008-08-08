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

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class QueryCancelRequest extends CDOClientRequest<Object>
{
  private static final ContextTracer PROTOCOL = new ContextTracer(OM.DEBUG_PROTOCOL, QueryCancelRequest.class);

  private int queryID;

  public QueryCancelRequest(int queryID, IChannel channel)
  {
    super(channel);
    this.queryID = queryID;
  }

  @Override
  protected short getSignalID()
  {
    return CDOProtocolConstants.SIGNAL_QUERY_CANCEL;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    if (PROTOCOL.isEnabled()) PROTOCOL.trace("Cancel query " + queryID);
    // Write queryID
    out.writeInt(queryID);
  }

  @Override
  protected Object confirming(ExtendedDataInputStream in) throws IOException
  {
    boolean exception = in.readBoolean();
    if (exception)
    {
      String exceptionMessage = in.readString();
      throw new RuntimeException(exceptionMessage);
    }
    return true;
  }
}
