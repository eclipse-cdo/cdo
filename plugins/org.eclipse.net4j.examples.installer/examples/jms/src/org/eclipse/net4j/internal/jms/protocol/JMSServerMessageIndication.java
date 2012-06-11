/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.internal.jms.ConnectionImpl;
import org.eclipse.net4j.internal.jms.MessageImpl;
import org.eclipse.net4j.internal.jms.util.MessageUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class JMSServerMessageIndication extends Indication
{
  /**
   * @since 2.0
   */
  public JMSServerMessageIndication(JMSClientProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_SERVER_MESSAGE);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int sessionID = in.readInt();
    long consumerID = in.readLong();
    MessageImpl message = MessageUtil.read(in);
    JMSClientProtocol protocol = (JMSClientProtocol)getProtocol();
    ConnectionImpl connection = protocol.getInfraStructure();
    connection.handleMessageFromSignal(sessionID, consumerID, message);
  }
}
