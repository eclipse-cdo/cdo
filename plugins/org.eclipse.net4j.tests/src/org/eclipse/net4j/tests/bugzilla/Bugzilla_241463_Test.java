/*
 * Copyright (c) 2010-2012, 2016, 2020, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - maintenance (SSL)
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.tests.bugzilla;

import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Test;

/**
 * See bug 241463.
 *
 * @author Eike Stepper
 */
public class Bugzilla_241463_Test extends AbstractConfigTest
{
  private final AtomicInteger protocolFactoryCalls = new AtomicInteger();

  /**
   * Runs this regression against every supported transport configuration.
   *
   * @return the configured transport regression suite
   */
  @SuppressWarnings("unchecked")
  public static Test suite()
  {
    Net4jTestSuite suite = new Net4jTestSuite(Bugzilla_241463_Test.class.getName(), JVM.class, TCP.class, SSL.class, WS.class, WSS.class);
    suite.addTestSuite(Bugzilla_241463_Test.class);
    return suite;
  }

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    container.registerFactory(new FailingTestSignalProtocolFactory(protocolFactoryCalls));
    return container;
  }

  public void testBugzilla241463() throws Exception
  {
    startTransport();
    getConnector().setOpenChannelTimeout(2000L);
    protocolFactoryCalls.set(0);

    try
    {
      new TestSignalProtocol(getConnector());
      fail("Opening a channel must fail when the server cannot create its protocol"); //$NON-NLS-1$
    }
    catch (Exception expected)
    {
      assertEquals("The server protocol factory must fail the inverse channel open", 1, protocolFactoryCalls.get()); //$NON-NLS-1$
    }
  }

  /**
   * Forces the server's inverse channel-open path to fail during protocol creation, which exercises the provider-neutral message
   * connector behavior used by both stream transports and WebSocket acceptors.
   *
   * @author Eike Stepper
   */
  private static final class FailingTestSignalProtocolFactory extends ServerProtocolFactory
  {
    private final AtomicInteger calls;

    private FailingTestSignalProtocolFactory(AtomicInteger calls)
    {
      super(TestSignalProtocol.PROTOCOL_NAME);
      this.calls = calls;
    }

    @Override
    public TestSignalProtocol create(String description) throws ProductCreationException
    {
      calls.incrementAndGet();
      throw new IllegalStateException("Simulated failure while creating the server protocol"); //$NON-NLS-1$
    }
  }
}
