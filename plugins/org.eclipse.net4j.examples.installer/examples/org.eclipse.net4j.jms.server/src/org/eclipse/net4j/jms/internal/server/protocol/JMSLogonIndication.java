/*
 * Copyright (c) 2007-2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.Server;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.bundle.OM;
import org.eclipse.net4j.jms.internal.server.messages.Messages;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSLogonIndication extends IndicationWithResponse
{
  private boolean ok;

  public JMSLogonIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_LOGON);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    String userName = in.readString();
    String password = in.readString();
    ServerConnection connection = Server.INSTANCE.logon(userName, password);
    if (connection == null)
    {
      OM.LOG.error(Messages.getString("JMSLogonIndication_0")); //$NON-NLS-1$
      return;
    }

    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    connection.setProtocol(protocol);
    protocol.setInfraStructure(connection);
    ok = true;
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(ok);
  }
}
