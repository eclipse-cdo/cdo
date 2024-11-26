/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RemoteTopicRequest extends CDOClientRequest<Set<Integer>>
{
  private String id;

  private boolean subscribe;

  public RemoteTopicRequest(CDOClientProtocol protocol, String id, boolean subscribe)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REMOTE_TOPIC);
    this.id = id;
    this.subscribe = subscribe;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeString(id);
    out.writeBoolean(subscribe);
  }

  @Override
  protected Set<Integer> confirming(CDODataInput in) throws IOException
  {
    if (subscribe)
    {
      Set<Integer> result = new HashSet<>();

      for (;;)
      {
        int sessionID = in.readXInt();
        if (sessionID == CDOProtocolConstants.NO_MORE_REMOTE_SESSIONS)
        {
          break;
        }

        result.add(sessionID);
      }

      return result;
    }

    in.readBoolean();
    return null;
  }

  @Override
  protected String getAdditionalInfo()
  {
    return MessageFormat.format("id={0}, subscribe={1}", id, subscribe);
  }
}
