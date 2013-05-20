/*
 * Copyright (c) 2007, 2008, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

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
    super(protocol, JMSProtocolConstants.SIGNAL_COMMIT);
    this.sessionID = sessionID;
    this.messages = messages;
  }

  @Override
  protected void requesting(ExtendedDataOutputStream out) throws Exception
  {
    out.writeInt(sessionID);
    out.writeInt(messages.size());
    for (MessageImpl message : messages)
    {
      MessageUtil.write(out, message);
    }
  }

  @Override
  protected String[] confirming(ExtendedDataInputStream in) throws Exception
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
