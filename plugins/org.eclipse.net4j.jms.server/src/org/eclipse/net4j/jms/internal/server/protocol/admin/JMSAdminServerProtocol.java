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
package org.eclipse.net4j.jms.internal.server.protocol.admin;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.jms.server.IServer;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSAdminServerProtocol extends SignalProtocol
{
  public JMSAdminServerProtocol()
  {
  }

  public String getType()
  {
    return JMSAdminProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor doCreateSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case JMSAdminProtocolConstants.SIGNAL_CREATE_DESTINATION:
      return new JMSCreateDestinationIndication();
    }

    return null;
  }

  public IServer getServer()
  {
    return IServer.INSTANCE;
  }
}
