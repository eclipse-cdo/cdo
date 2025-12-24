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
package org.eclipse.net4j.jms.internal.server.protocol.admin;

import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.jms.server.IDestination;
import org.eclipse.net4j.jms.server.IDestination.Type;
import org.eclipse.net4j.jms.server.IServer;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSCreateDestinationIndication extends IndicationWithResponse
{
  private boolean ok;

  public JMSCreateDestinationIndication(JMSAdminServerProtocol protocol)
  {
    super(protocol, JMSAdminProtocolConstants.SIGNAL_CREATE_DESTINATION);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    byte type = in.readByte();
    String name = in.readString();
    JMSAdminServerProtocol protocol = (JMSAdminServerProtocol)getProtocol();
    IServer server = protocol.getServer();
    IDestination destination = server.createDestination(name, getDestinationType(type));
    if (destination != null)
    {
      ok = true;
    }
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeBoolean(ok);
  }

  private Type getDestinationType(byte type)
  {
    switch (type)
    {
    case JMSAdminProtocolConstants.DESTINATION_TYPE_QUEUE:
      return IDestination.Type.QUEUE;
    case JMSAdminProtocolConstants.DESTINATION_TYPE_TOPIC:
      return IDestination.Type.TOPIC;
    }

    throw new IllegalArgumentException("type: " + type); //$NON-NLS-1$
  }
}
