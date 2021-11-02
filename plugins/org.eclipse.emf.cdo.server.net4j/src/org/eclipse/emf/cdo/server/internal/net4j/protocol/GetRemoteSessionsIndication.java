/*
 * Copyright (c) 2009-2012, 2017, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class GetRemoteSessionsIndication extends CDOServerReadIndication
{
  private boolean subscribe;

  public GetRemoteSessionsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_GET_REMOTE_SESSIONS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    subscribe = in.readBoolean();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession localSession = getSession();
    InternalSession[] sessions = localSession.getManager().getSessions();

    for (InternalSession session : sessions)
    {
      if (session != localSession)
      {
        out.writeXInt(session.getSessionID());
        out.writeString(session.getUserID());
        out.writeBoolean(session.isSubscribed());
      }
    }

    out.writeXInt(CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS);
    localSession.setSubscribed(subscribe);
  }
}
