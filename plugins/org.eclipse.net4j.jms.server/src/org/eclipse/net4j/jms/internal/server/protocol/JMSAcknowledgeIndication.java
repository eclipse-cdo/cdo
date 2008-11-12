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
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.ServerSession;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.signal.Indication;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * @author Eike Stepper
 */
public class JMSAcknowledgeIndication extends Indication
{
  public JMSAcknowledgeIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_ACKNOWLEDGE);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int sessionID = in.readInt();

    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    ServerConnection connection = protocol.getInfraStructure();
    ServerSession session = connection.getSession(sessionID);
    if (session == null)
    {
      OM.LOG.info("Session " + sessionID + " not found. Acknowledgement discarded.");
      return;
    }

    session.handleAcknowledge();
  }
}
