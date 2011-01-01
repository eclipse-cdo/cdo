/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.defs.util;

import org.eclipse.net4j.FactoriesProtocolProvider;
import org.eclipse.net4j.defs.Net4jDefsFactory;
import org.eclipse.net4j.defs.ProtocolProviderDef;
import org.eclipse.net4j.defs.TCPConnectorDef;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.util.defs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.factory.IFactory;

import java.util.Iterator;
import java.util.List;

public class Net4jDefsUtil
{
  /**
   * Creates the factories protocol provider. The current implementation uses a {@link FactoriesProtocolProvider}.
   * 
   * @param clientProtocolFactoryDef
   *          the client protocol factory definition
   * @return the protocol provider
   */
  public static <PPD extends ProtocolProviderDef> IProtocolProvider createFactoriesProtocolProvider(
      List<PPD> protocolFactoryDef)
  {
    FactoriesProtocolProvider protocolProvider = new FactoriesProtocolProvider();
    for (Iterator<PPD> iterator = protocolFactoryDef.iterator(); iterator.hasNext();)
    {
      PPD protocolProviderDef = iterator.next();
      protocolProvider.addFactory((IFactory)protocolProviderDef.getInstance());
    }
    return protocolProvider;
  }

  public static TCPConnectorDef createTCPConnectorDef(final String host)
  {
    return createTCPConnectorDef(host, ITCPConnector.DEFAULT_PORT);
  }

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
