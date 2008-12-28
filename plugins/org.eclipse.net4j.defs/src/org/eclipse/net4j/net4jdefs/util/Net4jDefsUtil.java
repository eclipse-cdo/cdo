package org.eclipse.net4j.net4jdefs.util;

import org.eclipse.net4j.FactoriesProtocolProvider;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.ProtocolProviderDef;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;

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
