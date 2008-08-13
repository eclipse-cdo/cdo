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
package org.eclipse.net4j.jms.internal.admin.protocol;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jms.JMSAdminProtocolConstants;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;

/**
 * @author Eike Stepper
 */
public class JMSAdminProtocol extends SignalProtocol
{
  public JMSAdminProtocol(IConnector connector)
  {
    super(connector);
  }

  public String getType()
  {
    return JMSAdminProtocolConstants.PROTOCOL_NAME;
  }

  @Override
  protected SignalReactor createSignalReactor(short signalID)
  {
    return null;
  }
}
