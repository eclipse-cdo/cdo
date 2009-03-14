/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.net4j.protocol;

import org.eclipse.emf.cdo.common.io.CDODataInput;
import org.eclipse.emf.cdo.common.io.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class GetRemoteSessionsRequest extends CDOClientRequest<List<CDORemoteSession>>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, GetRemoteSessionsRequest.class);

  private InternalCDORemoteSessionManager manager;

  private boolean subscribe;

  public GetRemoteSessionsRequest(CDOClientProtocol protocol, InternalCDORemoteSessionManager manager, boolean subscribe)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS);
    this.manager = manager;
    this.subscribe = subscribe;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing subscribe: {0}", subscribe);
    }

    out.writeBoolean(subscribe);
  }

  @Override
  protected List<CDORemoteSession> confirming(CDODataInput in) throws IOException
  {
    List<CDORemoteSession> result = new ArrayList<CDORemoteSession>();
    for (;;)
    {
      int sessionID = in.readInt();
      if (sessionID == CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS)
      {
        break;
      }

      String userID = in.readString();
      boolean subscribed = in.readBoolean();
      CDORemoteSession remoteSession = manager.createRemoteSession(sessionID, userID, subscribed);
      result.add(remoteSession);
    }

    return result;
  }
}
