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

import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.net4jdefs.ChallengeNegotiatorDef;
import org.eclipse.net4j.net4jdefs.Net4jDefsFactory;
import org.eclipse.net4j.net4jdefs.TCPAcceptorDef;
import org.eclipse.net4j.net4jdefs.User;
import org.eclipse.net4j.net4jdefs.UserManagerDef;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.net4jutildefs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.tests.AbstractOMTest;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorDefImplTest extends AbstractOMTest
{
  private static final long TIMEOUT = 10000;

  private static final long DELAY = 500;

  private static final String HOST = "localhost";

  private static final int PORT = 2036;

  private static final String USERID = "André";

  private static final String PASSWORD = "aPassword";

  private TCPConnector tcpConnector;

  @Override
  protected void doSetUp() throws Exception
  {
    tcpConnector = Util.createTCPClientConnector(HOST, PORT, Util.createTCPSelector());
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(tcpConnector);
  }

  public void testAcceptorDefOpensConnection()
  {
    TCPAcceptorDef tcpAcceptorDef = createTCPAcceptorDef();

    ITCPAcceptor tcpAcceptor = (ITCPAcceptor)tcpAcceptorDef.getInstance();

    assertTrue(LifecycleUtil.isActive(tcpAcceptor));

    LifecycleUtil.activate(tcpConnector);
    boolean connected = tcpConnector.waitForConnection(DELAY + TIMEOUT);

    assertTrue(connected);
    assertTrue(LifecycleUtil.isActive(tcpConnector));

    LifecycleUtil.deactivate(tcpAcceptor);
  }

  /**
   * Doesn't work yet: UserManager does not add its users yet
   */
  public void _testCredentialsProvider()
  {
    TCPAcceptorDef tcpAcceptorDef = createTCPAcceptorDef();

    User user = Net4jDefsFactory.eINSTANCE.createUser();
    user.setUserID(USERID);
    user.setPassword(PASSWORD);

    UserManagerDef userManagerDef = Net4jDefsFactory.eINSTANCE.createUserManagerDef();
    userManagerDef.getUser().add(user);

    ChallengeNegotiatorDef challengeNegotiatorDef = Net4jDefsFactory.eINSTANCE.createChallengeNegotiatorDef();
    challengeNegotiatorDef.setRandomizer(Net4jDefsFactory.eINSTANCE.createRandomizerDef());
    challengeNegotiatorDef.setUserManager(userManagerDef);

    tcpAcceptorDef.setNegotiator(challengeNegotiatorDef);
    ITCPAcceptor tcpAcceptor = (ITCPAcceptor)tcpAcceptorDef.getInstance();
    assertTrue(LifecycleUtil.isActive(tcpAcceptor));

    Util.addNegotiator(USERID, PASSWORD, tcpConnector);
    LifecycleUtil.activate(tcpConnector);

    boolean connected = tcpConnector.waitForConnection(DELAY + TIMEOUT);
    assertTrue(connected);

    LifecycleUtil.deactivate(tcpConnector);

    Util.removeNegotiator(tcpConnector);
    LifecycleUtil.deactivate(tcpAcceptor);
  }

  private TCPAcceptorDef createTCPAcceptorDef()
  {
    TCPAcceptorDef tcpAcceptorDef = Net4jDefsFactory.eINSTANCE.createTCPAcceptorDef();
    tcpAcceptorDef.setHost(HOST);
    tcpAcceptorDef.setPort(PORT);
    tcpAcceptorDef.setBufferProvider(Net4jDefsFactory.eINSTANCE.createBufferPoolDef());
    tcpAcceptorDef.setExecutorService(Net4jUtilDefsFactory.eINSTANCE.createThreadPoolDef());
    tcpAcceptorDef.setTcpSelectorDef(Net4jDefsFactory.eINSTANCE.createTCPSelectorDef());
    return tcpAcceptorDef;
  }
}
