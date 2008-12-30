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

import org.eclipse.net4j.net4jdefs.BufferPoolDef;
import org.eclipse.net4j.net4jdefs.BufferProviderDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.net4jdefs.TCPSelectorDef;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.net4jutildefs.ExecutorServiceDef;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.net4jutildefs.ThreadPoolDef;

/**
 * @author Eike Stepper
 */
public class TCPClientConnectorBuilder
{
  private String host;

  private int port = ITCPConnector.DEFAULT_PORT;

  private BufferProviderDef bufferProviderDef = Net4jDefsFactory.eINSTANCE.createBufferPoolDef();

  private ExecutorServiceDef executorService = Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef();

  private TCPSelectorDef tcpSelectorDef = Net4jDefsFactory.eINSTANCE.createTCPSelectorDef();

  public TCPClientConnectorBuilder()
  {
  }

  public TCPClientConnectorBuilder port(int port)
  {
    this.port = port;
    return this;
  }

  public TCPClientConnectorBuilder host(String host)
  {
    this.host = host;
    return this;
  }

  public TCPClientConnectorBuilder executorServiceDef(ThreadPoolDef threadPoolDef)
  {
    executorService = threadPoolDef;
    return this;
  }

  public TCPClientConnectorBuilder bufferDef(BufferPoolDef bufferPoolDef)
  {
    bufferProviderDef = bufferPoolDef;
    return this;
  }

  public TCPConnectorDef build()
  {
    TCPConnectorDef tcpClientConnectorDef = Net4jDefsFactory.eINSTANCE.createTCPConnectorDef();

    CheckUtil.checkState(host != null, "host is not set!");
    tcpClientConnectorDef.setHost(host);

    tcpClientConnectorDef.setPort(port);

    tcpClientConnectorDef.setBufferProvider(bufferProviderDef);
    tcpClientConnectorDef.setExecutorService(executorService);
    tcpClientConnectorDef.setTcpSelectorDef(tcpSelectorDef);

    return tcpClientConnectorDef;
  }
}
