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
package org.eclipse.net4j.jms.internal.server.protocol.admin;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.jms.server.IServer;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSAdminServerProtocol extends SignalProtocol<Object>
{
  public JMSAdminServerProtocol()
  {
    super(JMSAdminProtocolConstants.PROTOCOL_NAME);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case JMSAdminProtocolConstants.SIGNAL_CREATE_DESTINATION:
      return new JMSCreateDestinationIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }

  public IServer getServer()
  {
    return IServer.INSTANCE;
  }
}
