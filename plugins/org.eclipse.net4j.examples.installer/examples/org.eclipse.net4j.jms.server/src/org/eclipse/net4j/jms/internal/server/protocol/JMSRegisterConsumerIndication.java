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
package org.eclipse.net4j.jms.internal.server.protocol;

import org.eclipse.net4j.internal.jms.DestinationImpl;
import org.eclipse.net4j.internal.jms.util.DestinationUtil;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.jms.internal.server.ServerConnection;
import org.eclipse.net4j.jms.internal.server.ServerSession;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * @author Eike Stepper
 */
public class JMSRegisterConsumerIndication extends IndicationWithResponse
{
  private long consumerID;

  public JMSRegisterConsumerIndication(JMSServerProtocol protocol)
  {
    super(protocol, JMSProtocolConstants.SIGNAL_REGISTER_CONSUMER);
  }

  @Override
  protected void indicating(ExtendedDataInputStream in) throws Exception
  {
    int sessionID = in.readInt();
    DestinationImpl destination = DestinationUtil.read(in);
    String messageSelector = in.readString();
    boolean noLocal = in.readBoolean();
    boolean durable = in.readBoolean();
    JMSServerProtocol protocol = (JMSServerProtocol)getProtocol();
    ServerConnection connection = protocol.getInfraStructure();
    ServerSession session = connection.getSession(sessionID);
    consumerID = session.registerConsumer(destination, messageSelector, noLocal, durable);
  }

  @Override
  protected void responding(ExtendedDataOutputStream out) throws Exception
  {
    out.writeLong(consumerID);
  }
}
