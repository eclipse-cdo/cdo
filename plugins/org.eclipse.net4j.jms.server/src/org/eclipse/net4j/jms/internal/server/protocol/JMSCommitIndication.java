/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.ServerSession;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.internal.server.messages.Messages;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class JMSCommitIndication extends IndicationWithResponse
{
  private String[] messageIDs;

  public JMSCommitIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_COMMIT);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int sessionID = in.readInt();
    int size = in.readInt();
    MessageImpl[] messages = new MessageImpl[size];
    for (int i = 0; i < messages.length; i++)
    {
      messages[i] = MessageUtil.read(in);
    }

    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    ServerConnection connection = protocol.getInfraStructure();
    ServerSession session = connection.getSession(sessionID);
    if (session == null)
    {
      OM.LOG.warn(MessageFormat.format(Messages.getString("JMSCommitIndication_0"), sessionID)); //$NON-NLS-1$
      return;
    }

    messageIDs = session.handleCommit(messages);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    if (messageIDs == null)
    {
      out.writeInt(-1);
    }
    else
    {
      out.writeInt(messageIDs.length);
      for (String messageID : messageIDs)
      {
        out.writeString(messageID);
      }
    }
  }
}
