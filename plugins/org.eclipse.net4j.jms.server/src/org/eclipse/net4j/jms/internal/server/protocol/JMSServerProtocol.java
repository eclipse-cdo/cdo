/*
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

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSServerProtocol extends SignalProtocol<ServerConnection>
{
  public JMSServerProtocol()
  {
    super(JMSProtocolConstants.PROTOCOL_NAME);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case JMSProtocolConstants.SIGNAL_SYNC:
      return new JMSSyncIndication(this);

    case JMSProtocolConstants.SIGNAL_LOGON:
      return new JMSLogonIndication(this);

    case JMSProtocolConstants.SIGNAL_OPEN_SESSION:
      return new JMSOpenSessionIndication(this);

    case JMSProtocolConstants.SIGNAL_REGISTER_CONSUMER:
      return new JMSRegisterConsumerIndication(this);

    case JMSProtocolConstants.SIGNAL_CLIENT_MESSAGE:
      return new JMSClientMessageIndication(this);

    case JMSProtocolConstants.SIGNAL_ACKNOWLEDGE:
      return new JMSAcknowledgeIndication(this);

    case JMSProtocolConstants.SIGNAL_RECOVER:
      return new JMSRecoverIndication(this);

    case JMSProtocolConstants.SIGNAL_COMMIT:
      return new JMSCommitIndication(this);

    case JMSProtocolConstants.SIGNAL_ROLLBACK:
      return new JMSRollbackIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
