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
package org.eclipse.net4j.tests.defs;

import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.PasswordCredentialsProviderDef;
import org.eclipse.net4j.net4jdefs.ResponseNegotiatorDef;
import org.eclipse.net4j.net4jdefs.TCPConnectorDef;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tests.AbstractProtocolTest;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;

/**
 * @author Eike Stepper
 */
public class TCPConnectorDefImplTest extends AbstractProtocolTest
{

  private static final long DELAY = 500l;

  private static final int PORT = 2036;

  private static final long TIMEOUT = 10000l;

  private static final String USERID = "André";

  private static final String PASSWORD = "aPassword";

  private TCPAcceptor tcpAcceptor;

  @Override
  protected void doSetUp() throws Exception
  {
    tcpAcceptor = Util.createTCPAcceptor(HOST, PORT, DELAY);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(tcpAcceptor);
  }

  public void testConnectorDefOpensConnection()
  {

    TCPConnectorDef tcpConnectorDef = Net4jDefsFactory.eINSTANCE.createTCPConnectorDef();

    tcpConnectorDef.setHost(HOST);
    tcpConnectorDef.setPort(PORT);
    tcpConnectorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    tcpConnectorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    tcpConnectorDef.setTcpSelectorDef(Net4jDefsFactory.eINSTANCE.createTCPSelectorDef());

    ITCPConnector tcpConnector = (ITCPConnector)tcpConnectorDef.getInstance();

    boolean connected = tcpConnector.waitForConnection(DELAY + TIMEOUT);
    assertTrue(connected);
    assertTrue(LifecycleUtil.isActive(tcpConnector));

    LifecycleUtil.deactivate(tcpConnector);
  }

  public void testNewInstanceWhenTouched()
  {
    TCPConnectorDef tcpConnectorDef = Net4jDefsFactory.eINSTANCE.createTCPConnectorDef();

    tcpConnectorDef.setHost(HOST);
    tcpConnectorDef.setPort(PORT);
    tcpConnectorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    tcpConnectorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    tcpConnectorDef.setTcpSelectorDef(Net4jDefsFactory.eINSTANCE.createTCPSelectorDef());

    ITCPConnector thisTcpConnector = (ITCPConnector)tcpConnectorDef.getInstance();
    tcpConnectorDef.setPort(PORT + 1);

    assertTrue(tcpConnectorDef.isTouched());

    ITCPConnector thatTcpConnector = (ITCPConnector)tcpConnectorDef.getInstance();

    assertTrue(!LifecycleUtil.isActive(thisTcpConnector));
    assertTrue(!tcpConnectorDef.isTouched());
    assertTrue(thisTcpConnector != thatTcpConnector);

    LifecycleUtil.deactivate(thisTcpConnector);
    LifecycleUtil.deactivate(thatTcpConnector);
  }

  public void testCredentialsProvider()
  {

    Util.addNegotiator(USERID, PASSWORD, tcpAcceptor);

    TCPConnectorDef tcpConnectorDef = Net4jDefsFactory.eINSTANCE.createTCPConnectorDef();

    tcpConnectorDef.setHost(HOST);
    tcpConnectorDef.setPort(PORT);
    tcpConnectorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    tcpConnectorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    tcpConnectorDef.setTcpSelectorDef(Net4jDefsFactory.eINSTANCE.createTCPSelectorDef());

    PasswordCredentialsProviderDef credentialsProviderDef = Net4jDefsFactory.eINSTANCE
        .createPasswordCredentialsProviderDef();
    credentialsProviderDef.setUserID(USERID);
    credentialsProviderDef.setPassword(PASSWORD);

    ResponseNegotiatorDef negotiatorDef = Net4jDefsFactory.eINSTANCE.createResponseNegotiatorDef();
    negotiatorDef.setCredentialsProvider(credentialsProviderDef);
    tcpConnectorDef.setNegotiator(negotiatorDef);

    ITCPConnector tcpConnector = (ITCPConnector)tcpConnectorDef.getInstance();

    boolean connected = tcpConnector.waitForConnection(DELAY + TIMEOUT);
    assertTrue(connected);
    assertTrue(LifecycleUtil.isActive(tcpConnector));

    LifecycleUtil.deactivate(tcpConnector);

    Util.removeNegotiator(tcpAcceptor);
  }

}
