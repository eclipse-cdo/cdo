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
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTopic;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RemoteMessageIndication extends CDOServerReadIndication
{
  private List<Integer> result;

  public RemoteMessageIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_MESSAGE);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDORemoteSessionMessage message = new CDORemoteSessionMessage(in);
    InternalSessionManager sessionManager = getRepository().getSessionManager();

    String topicID = in.readString();
    if (topicID != null)
    {
      InternalTopic topic = sessionManager.getTopicManager().getTopic(topicID);
      if (topic != null)
      {
        result = sessionManager.sendRemoteMessageNotification(getSession(), message, topic);
      }
    }
    else
    {
      int count = in.readXInt();

      int[] recipients = new int[count];
      for (int i = 0; i < recipients.length; i++)
      {
        recipients[i] = in.readXInt();
      }

      result = sessionManager.sendRemoteMessageNotification(getSession(), message, recipients);
    }
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    out.writeXInt(result.size());
    for (Integer recipient : result)
    {
      out.writeXInt(recipient);
    }
  }
}
