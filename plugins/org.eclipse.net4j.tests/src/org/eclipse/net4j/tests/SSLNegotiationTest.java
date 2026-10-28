/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.tests.config.TestConfig.TCP;
import org.eclipse.net4j.tests.config.TestConfig.WS;
import org.eclipse.net4j.tests.config.TestConfig.WSS;
import org.eclipse.net4j.util.security.PasswordCredentials;

/**
 * Verifies Net4j negotiation over an SSL transport.
 *
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, WS.class, WSS.class })
public class SSLNegotiationTest extends AbstractConfigTest
{
  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final char[] PASSWORD = "eike2008".toCharArray(); //$NON-NLS-1$

  @Override
  protected TestConfig.Factory getDefaultFactory()
  {
    return new TestConfig.SSL();
  }

  public void testNegotiationSuccess() throws Exception
  {
    PasswordCredentials credentials = new PasswordCredentials(USER_ID, PASSWORD);
    acceptorContainer.addPostProcessor(new NegotiationTest.AcceptorNegotiatorInjector(USER_ID, PASSWORD));
    connectorContainer.addPostProcessor(new NegotiationTest.ConnectorNegotiatorInjector(credentials));

    startTransport();

    IConnector connector = getConnector();
    connector.waitForConnection(DEFAULT_TIMEOUT);

    IChannel channel = connector.openChannel();
    assertEquals(USER_ID, channel.getUserID());
  }
}
