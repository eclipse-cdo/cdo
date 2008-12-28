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

public class TCPClientConnectorBuilder
{

  private int port = ITCPConnector.DEFAULT_PORT;

  private String host;

  private BufferProviderDef bufferProviderDef = Net4jDefsFactory.eINSTANCE.createBufferPoolDef();

  private ExecutorServiceDef executorService = Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef();

  private TCPSelectorDef tcpSelectorDef = Net4jDefsFactory.eINSTANCE.createTCPSelectorDef();

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
    this.executorService = threadPoolDef;
    return this;
  }

  public TCPClientConnectorBuilder bufferDef(BufferPoolDef bufferPoolDef)
  {
    this.bufferProviderDef = bufferPoolDef;
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
