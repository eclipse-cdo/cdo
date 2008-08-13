/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class JMSCommitRequest extends RequestWithConfirmation<String[]>
{
  private int sessionID;

  private List<MessageImpl> messages;

  /**
   * @since 2.0
   */
  public JMSCommitRequest(JMSClientProtocol protocol, int sessionID, List<MessageImpl> messages)
  {
    super(protocol);
    this.sessionID = sessionID;
    this.messages = messages;
  }

  @Override
  protected short getSignalID()
  {
    return JMSProtocolConstants.SIGNAL_COMMIT;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws IOException
  {
    out.writeInt(sessionID);
    out.writeInt(messages.size());
    for (MessageImpl message : messages)
    {
      MessageUtil.write(out, message);
    }
  }

  @Override
  protected String[] confirming(ExtendedDataInputStream in) throws IOException
  {
    int size = in.readInt();
    if (size == -1)
    {
      return null;
    }

    String[] messageIDs = new String[size];
    for (int i = 0; i < size; i++)
    {
      messageIDs[i] = in.readString();
    }

    return messageIDs;
  }
}
