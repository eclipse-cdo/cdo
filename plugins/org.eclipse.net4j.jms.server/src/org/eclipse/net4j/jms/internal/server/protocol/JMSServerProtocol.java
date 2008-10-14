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
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSServerProtocol extends SignalProtocol
{
  public JMSServerProtocol()
  {
  }

  public String getType()
  {
    return JMSProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case JMSProtocolConstants.SIGNAL_SYNC:
      return new JMSSyncIndication();

    case JMSProtocolConstants.SIGNAL_LOGON:
      return new JMSLogonIndication();

    case JMSProtocolConstants.SIGNAL_OPEN_SESSION:
      return new JMSOpenSessionIndication();

    case JMSProtocolConstants.SIGNAL_REGISTER_CONSUMER:
      return new JMSRegisterConsumerIndication();

    case JMSProtocolConstants.SIGNAL_CLIENT_MESSAGE:
      return new JMSClientMessageIndication();

    case JMSProtocolConstants.SIGNAL_ACKNOWLEDGE:
      return new JMSAcknowledgeIndication();

    case JMSProtocolConstants.SIGNAL_RECOVER:
      return new JMSRecoverIndication();

    case JMSProtocolConstants.SIGNAL_COMMIT:
      return new JMSCommitIndication();

    case JMSProtocolConstants.SIGNAL_ROLLBACK:
      return new JMSRollbackIndication();

    default:
      return null;
    }
  }
}
