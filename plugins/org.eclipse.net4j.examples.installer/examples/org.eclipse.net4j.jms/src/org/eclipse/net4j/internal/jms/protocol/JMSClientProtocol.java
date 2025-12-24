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
package org.eclipse.net4j.internal.jms.protocol;

import org.eclipse.net4j.internal.jms.ConnectionImpl;
import org.eclipse.net4j.jms.JMSProtocolConstants;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSClientProtocol extends SignalProtocol<ConnectionImpl>
{
  /**
   * @since 2.0
   */
  public JMSClientProtocol(ConnectionImpl connection)
  {
    super(JMSProtocolConstants.PROTOCOL_NAME);
    setInfraStructure(connection);
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    switch (signalID)
    {
    case JMSProtocolConstants.SIGNAL_SERVER_MESSAGE:
      return new JMSServerMessageIndication(this);

    default:
      return super.createSignalReactor(signalID);
    }
  }
}
