/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.TCPConstants;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorFactory;

/**
 * @author Eike Stepper
 */
public class TCPConnectorFactoryImpl implements ConnectorFactory
{
  public String getType()
  {
    return TCPConstants.TYPE;
  }

  public Connector createConnector()
  {
    return new ClientTCPConnectorImpl();
  }
}
