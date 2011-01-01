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
package org.eclipse.net4j.tests.defs;

import org.eclipse.net4j.defs.Net4jDefsFactory;
import org.eclipse.net4j.defs.TCPAcceptorDef;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.util.defs.ChallengeNegotiatorDef;
import org.eclipse.net4j.util.defs.Net4jUtilDefsFactory;
import org.eclipse.net4j.util.defs.User;
import org.eclipse.net4j.util.defs.UserManagerDef;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.tests.AbstractOMTest;

/**
 * @author Andre Dietisheim
 */
public class TCPAcceptorDefImplTest extends AbstractOMTest
{
  private static final long TIMEOUT = 10000;

  private static final long DELAY = 500;

  private static final String HOST = "localhost"; //$NON-NLS-1$

  private static final int PORT = 2036;

  private static final String USERID = "André"; //$NON-NLS-1$

  private static final String PASSWORD = "aPassword"; //$NON-NLS-1$

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
    tcpConnector.waitForConnection(DELAY + TIMEOUT);
    assertTrue(LifecycleUtil.isActive(tcpConnector));

    LifecycleUtil.deactivate(tcpAcceptor);
  }

  /**
   * Doesn't work yet: UserManager does not add its users yet
   */
  public void _testCredentialsProvider()
  {
    TCPAcceptorDef tcpAcceptorDef = createTCPAcceptorDef();

    User user = Net4jUtilDefsFactory.eINSTANCE.createUser();
    user.setUserID(USERID);
    user.setPassword(PASSWORD);

    UserManagerDef userManagerDef = Net4jUtilDefsFactory.eINSTANCE.createUserManagerDef();
    userManagerDef.getUser().add(user);

    ChallengeNegotiatorDef challengeNegotiatorDef = Net4jUtilDefsFactory.eINSTANCE.createChallengeNegotiatorDef();
    challengeNegotiatorDef.setRandomizer(Net4jUtilDefsFactory.eINSTANCE.createRandomizerDef());
    challengeNegotiatorDef.setUserManager(userManagerDef);

    tcpAcceptorDef.setNegotiator(challengeNegotiatorDef);
    ITCPAcceptor tcpAcceptor = (ITCPAcceptor)tcpAcceptorDef.getInstance();
    assertTrue(LifecycleUtil.isActive(tcpAcceptor));

    Util.addNegotiator(USERID, PASSWORD, tcpConnector);
    LifecycleUtil.activate(tcpConnector);

    tcpConnector.waitForConnection(DELAY + TIMEOUT);
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
