/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.SSL;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.data.TinyData;
import org.eclipse.net4j.tests.signal.ArrayRequest;
import org.eclipse.net4j.tests.signal.TestSignalProtocol;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.Arrays;

/**
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, SSL.class }) // Test only Websocket heartbeat pongs.
public class IdleTimeoutTest extends AbstractConfigTest
{
  public void testSingleThreadTinyDataLongWithPause() throws Exception
  {
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

    log("Pausing 40 seconds ...");
    sleep(40000); // Pause longer than the 30000 millisecond timeouts.

    log("Sending 10000 signals...");
    for (int i = 0; i < 10000; i++)
    {
      byte[] result = new ArrayRequest(protocol, data).send();
      assertEquals(true, Arrays.equals(data, result));
    }

    protocol.close();
    assertInactive(protocol);
  }

  private static void log(String message)
  {
    IOUtil.OUT().println(message);
  }
}
