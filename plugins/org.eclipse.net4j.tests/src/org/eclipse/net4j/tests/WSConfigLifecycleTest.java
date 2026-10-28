/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.tests.signal.IntRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.lifecycle.ILifecycle;

import junit.framework.Test;

/**
 * Checks that WS test endpoints follow managed-container replacement.
 *
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, SSL.class })
public class WSConfigLifecycleTest extends AbstractConfigTest
{
  /**
   * Runs the container-restart regression with both WebSocket transports.
   *
   * @return the focused WS and WSS lifecycle suite
   */
  @SuppressWarnings("unchecked")
  public static Test suite()
  {
    Net4jTestSuite suite = new Net4jTestSuite(WSConfigLifecycleTest.class.getName(), WS.class, WSS.class);
    suite.addTestSuite(WSConfigLifecycleTest.class);
    return suite;
  }

  public void testContainerRestartUsesNewAcceptor() throws Exception
  {
    startTransport();
    assertSignalRoundTrip();

    restartContainer();
    assertSignalRoundTrip();
  }

  public void testUnderlyingWebSocketClosureTerminatesClientProtocol() throws Exception
  {
    startTransport();
    TestSignalProtocol protocol = new TestSignalProtocol(getConnector());
    ILifecycle lifecycle = protocol;
    IConnector acceptedConnector = getServerConnector();
    IAcceptor acceptor = getAcceptor();
    try
    {
      config.closeUnderlyingConnection(acceptedConnector);
      assertNoTimeout(() -> acceptor.getAcceptedConnectors().length == 0);
      assertNoTimeout(() -> !lifecycle.isActive());
    }
    finally
    {
      protocol.close();
    }
  }

  private void assertSignalRoundTrip() throws Exception
  {
    TestSignalProtocol protocol = new TestSignalProtocol(getConnector());
    try
    {
      int value = 0x5A17;
      assertEquals(value, new IntRequest(protocol, value).send().intValue());
    }
    finally
    {
      protocol.close();
    }
  }
}
