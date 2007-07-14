/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.internal.jms.bundle.OM;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.ServerSession;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class JMSRollbackIndication extends IndicationWithResponse
{
  public JMSRollbackIndication()
  {
  }

  @Override
  protected short getSignalID()
  {
    return JMSProtocolConstants.SIGNAL_ROLLBACK;
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws IOException
  {
    int sessionID = in.readInt();
    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    ServerConnection connection = protocol.getConnection();
    ServerSession session = connection.getSession(sessionID);
    if (session == null)
    {
      OM.LOG.warn("Session " + sessionID + " not found");
      return;
    }

    session.handleRecover();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws IOException
  {
    out.writeBoolean(true);
  }
}
