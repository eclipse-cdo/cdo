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
