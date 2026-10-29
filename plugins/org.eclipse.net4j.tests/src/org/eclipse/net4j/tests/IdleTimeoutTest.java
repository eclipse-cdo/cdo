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
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.ws.jdk.JDKWSOptions;

import java.time.Duration;
import java.util.Arrays;

import junit.framework.Test;

/**
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, SSL.class }) // Test only Websocket heartbeat pongs.
public class IdleTimeoutTest extends AbstractConfigTest
{
  public static Test suite()
  {
    @SuppressWarnings("unchecked")
    Net4jTestSuite suite = new Net4jTestSuite(IdleTimeoutTest.class.getName(), JVM.class, TCP.class, SSL.class, WS.class, WSS.class);
    suite.addTestSuite(IdleTimeoutTest.class);
    return suite;
  }

  public void testSingleThreadTinyDataLongWithPause() throws Exception
  {
    getConfig().configureWebSocketIdleTimeout(Duration.ofMillis(1000), JDKWSOptions.builder().heartbeatInterval(Duration.ofMillis(200)).build());
    startTransport();

    IConnector connector = getConnector();
    connector.setOpenChannelTimeout(20000);

    TestSignalProtocol protocol = new TestSignalProtocol(connector);
    assertActive(protocol);

    byte[] data = TinyData.getBytes();

    log("Sending 10000 signals...");
    for (int i = 0; i < 10000; i++)
    {
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }

    log("Pausing 2.5 seconds, longer than the 1 second WebSocket idle timeout ...");
    sleep(2500);

    log("Sending 10000 signals...");
    for (int i = 0; i < 10000; i++)
    {
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }

    protocol.close();
    assertInactive(protocol);
  }

  public void testWebSocketIdleTimeoutClosesWithoutFrequentHeartbeats() throws Exception
  {
    getConfig().configureWebSocketIdleTimeout(Duration.ofMillis(1000), JDKWSOptions.builder().heartbeatInterval(Duration.ofMillis(1500)).build());
    startTransport();

    IConnector connector = getConnector();
    TestSignalProtocol protocol = new TestSignalProtocol(connector);
    assertActive(protocol);
    new ArrayRequest(protocol, TinyData.getBytes()).send();

    log("Pausing 2.5 seconds without a heartbeat before the 1 second WebSocket idle timeout ...");
    sleep(2500);
    assertFalse("The Jetty WebSocket idle timeout must close a connection without timely heartbeats", connector.isConnected()); //$NON-NLS-1$
  }

  private static void log(String message)
  {
    IOUtil.OUT().println(message);
  }
}
