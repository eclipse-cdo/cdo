/*
 * Copyright (c) 2009-2012, 2017 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.server.InternalTopicManager;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RemoteTopicIndication extends CDOServerReadIndication
{
  private String id;

  private boolean subscribe;

  public RemoteTopicIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_TOPIC);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    id = in.readString();
    subscribe = in.readBoolean();
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    InternalSession localSession = getSession();
    InternalTopicManager topicManager = getSession().getManager().getTopicManager();

    if (subscribe)
    {
      for (InternalSession session : topicManager.addSubscription(id, localSession))
      {
        out.writeXInt(session.getSessionID());
      }

      out.writeXInt(CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS);
    }
    else
    {
      topicManager.removeSubscription(id, localSession);
      out.writeBoolean(true);
    }
  }
}
