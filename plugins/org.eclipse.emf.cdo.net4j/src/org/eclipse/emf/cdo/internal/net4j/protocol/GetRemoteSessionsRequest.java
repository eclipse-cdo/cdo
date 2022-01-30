/*
 * Copyright (c) 2009-2012, 2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;

import org.eclipse.emf.spi.cdo.InternalCDORemoteSessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class GetRemoteSessionsRequest extends CDOClientRequest<List<CDORemoteSession>>
{
  private boolean subscribe;

  public GetRemoteSessionsRequest(CDOClientProtocol protocol, boolean subscribe)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS);
    this.subscribe = subscribe;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeBoolean(subscribe);
  }

  @Override
  protected List<CDORemoteSession> confirming(CDODataInput in) throws IOException
  {
    List<CDORemoteSession> result = new ArrayList<>();
    InternalCDORemoteSessionManager manager = getSession().getRemoteSessionManager();

    for (;;)
    {
      int sessionID = in.readXInt();
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
