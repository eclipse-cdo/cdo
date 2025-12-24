/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.Server;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSClientMessageIndication extends IndicationWithResponse
{
  private String messageID;

  public JMSClientMessageIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_CLIENT_MESSAGE);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    MessageImpl[] messages = { MessageUtil.read(in) };
    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    ServerConnection connection = protocol.getInfraStructure();
    Server server = connection.getServer();
    String[] ids = server.handleClientMessages(messages);
    if (ids != null && ids.length != 0)
    {
      messageID = ids[0];
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeString(messageID);
  }
}
