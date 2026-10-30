/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tcp.ITCPConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.spi.net4j.Acceptor;
import org.eclipse.spi.net4j.Acceptor.ConnectorPreparer;
import org.eclipse.spi.net4j.InternalConnector;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Test;

/**
 * @author Eike Stepper
 */
public class AcceptorTest extends AbstractConfigTest
{
  /**
   * Creates this test class under every supported transport configuration.
   *
   * @return the configured test suite
   */
  public static Test suite()
  {
    @SuppressWarnings("unchecked")
    Net4jTestSuite suite = new Net4jTestSuite(AcceptorTest.class.getName(), JVM.class, TCP.class, SSL.class, WS.class, WSS.class);
    suite.addTestSuite(AcceptorTest.class);
    return suite;
  }

  public void testDeferredAccept() throws Exception
  {
    for (int i = 0; i < 5; i++)
    {
      IOUtil.OUT().println(" RUN = " + i); //$NON-NLS-1$

      Acceptor acceptor = (Acceptor)getAcceptor();
      CountDownLatch preparationCompleted = new CountDownLatch(1);
      AtomicReference<LifecycleState> connectorStateBeforePreparationDelay = new AtomicReference<>();
      AtomicReference<LifecycleState> connectorStateAfterPreparationDelay = new AtomicReference<>();
      AtomicInteger acceptedConnectorsBeforePreparationDelay = new AtomicInteger(-1);
      AtomicInteger acceptedConnectorsAfterPreparationDelay = new AtomicInteger(-1);
      acceptor.setConnectorPreparer(new ConnectorPreparer()
      {
        @Override
        public void prepareConnector(InternalConnector connector)
        {
          connectorStateBeforePreparationDelay.set(LifecycleUtil.getLifecycleState(connector));
          acceptedConnectorsBeforePreparationDelay.set(acceptor.getAcceptedConnectors().length);
          sleep(500);
          connectorStateAfterPreparationDelay.set(LifecycleUtil.getLifecycleState(connector));
          acceptedConnectorsAfterPreparationDelay.set(acceptor.getAcceptedConnectors().length);
          preparationCompleted.countDown();
        }
      });

      IConnector connector = getConnector();
      connector.waitForConnection(DEFAULT_TIMEOUT);
      assertEquals(false, connector.isClosed());
      assertTrue("Connector preparation did not complete", preparationCompleted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)); //$NON-NLS-1$
      assertEquals("Server connector was active when preparation started", LifecycleState.INACTIVE, connectorStateBeforePreparationDelay.get()); //$NON-NLS-1$
      assertEquals("Server connector was accepted before preparation started", 0, acceptedConnectorsBeforePreparationDelay.get()); //$NON-NLS-1$
      assertEquals("Server connector was active when preparation finished", LifecycleState.INACTIVE, connectorStateAfterPreparationDelay.get()); //$NON-NLS-1$
      assertEquals("Server connector was accepted before preparation finished", 0, acceptedConnectorsAfterPreparationDelay.get()); //$NON-NLS-1$

      stopTransport();

      assertFalse("Client connector remains active after transport shutdown", LifecycleUtil.isActive(connector)); //$NON-NLS-1$
      assertEquals("Acceptor retained accepted connectors after transport shutdown", 0, acceptor.getAcceptedConnectors().length); //$NON-NLS-1$
      assertFalse("Acceptor remains active after transport shutdown", LifecycleUtil.isActive(acceptor)); //$NON-NLS-1$

      if (connector instanceof ITCPConnector)
      {
        // Rapidly reusing the TCP endpoint can time out before the previous connection teardown has settled.
        sleep(1000);
      }
    }
  }
}
