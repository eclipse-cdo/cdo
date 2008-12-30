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
package org.eclipse.net4j.net4jdefs.util;

import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;

/**
 * @author Eike Stepper
 */
public abstract class Net4jDefsUtil
{
  private Net4jDefsUtil()
  {
  }

  /*
   * TODO: Is this only used by tests? --> Move to tests
   */
  public static TCPConnectorDef createTCPConnectorDef(final String host)
  {
    return createTCPConnectorDef(host, ITCPConnector.DEFAULT_PORT);
  }

  /*
   * TODO: Is this only used by tests? --> Move to tests
   */
  public static TCPConnectorDef createTCPConnectorDef(final String host, final int port)
  {
    TCPConnectorDef tcpConnectorDef = Net4jDefsFactory.eINSTANCE.createTCPConnectorDef();
    tcpConnectorDef.setHost(host);
    tcpConnectorDef.setPort(port);
    tcpConnectorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    tcpConnectorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    tcpConnectorDef.setTcpSelectorDef(Net4jDefsFactory.eINSTANCE.createTCPSelectorDef());
    return tcpConnectorDef;
  }
}
