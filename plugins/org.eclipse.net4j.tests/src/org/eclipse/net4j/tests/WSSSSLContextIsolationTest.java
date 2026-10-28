/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.ws.WSConnectorFactory;
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
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.jdk.JDKWSUtil;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.security.KeyStore;
import java.security.SecureRandom;

import junit.framework.Test;

/**
 * Verifies that JDK WebSocket providers keep their TLS contexts isolated.
 *
 * @author Eike Stepper
 */
@ExcludedConfig({ JVM.class, TCP.class, SSL.class, WS.class })
public class WSSSSLContextIsolationTest extends AbstractConfigTest
{
  private static final String UNTRUSTED_PROVIDER_TYPE = "jdk-wss-empty-trust"; //$NON-NLS-1$

  @Override
  protected WSS getDefaultFactory()
  {
    return new WSS();
  }

  /**
   * Runs the isolation check against WSS.
   *
   * @return the WSS TLS context isolation suite
   */
  @SuppressWarnings("unchecked")
  public static Test suite()
  {
    Net4jTestSuite suite = new Net4jTestSuite(WSSSSLContextIsolationTest.class.getName(), WSS.class);
    suite.addTestSuite(WSSSSLContextIsolationTest.class);
    return suite;
  }

  public void testProviderTLSContextsAreIsolated() throws Exception
  {
    startTransport();
    IWSConnector trustedConnector = (IWSConnector)getConnector();
    int acceptedConnectors = getAcceptor().getAcceptedConnectors().length;

    SSLContext emptyTrustContext = createEmptyTrustContext();
    IFactory untrustedProvider = JDKWSUtil.createMessageConnectionFactory(UNTRUSTED_PROVIDER_TYPE, emptyTrustContext);
    IManagedContainer container = connectorContainer;
    container.registerFactory(untrustedProvider);

    String description = WSSUtil.getConnectorDescription(trustedConnector.getServiceURI(), trustedConnector.getAcceptorName(),
        WSConnectorFactory.PROVIDER_TOKEN_PREFIX + UNTRUSTED_PROVIDER_TYPE);
    IConnector untrustedConnector = (IConnector)container.getElement(WSConnectorFactory.PRODUCT_GROUP, WSSUtil.FACTORY_TYPE, description, false);
    untrustedConnector.setOpenChannelTimeout(2000L);

    try
    {
      try
      {
        LifecycleUtil.activate(untrustedConnector);
        new TestSignalProtocol(untrustedConnector);
        fail("The provider with an empty trust store must not connect to the fixture certificate"); //$NON-NLS-1$
      }
      catch (Exception expected)
      {
        assertEquals("The untrusted provider must not add an accepted Net4j connector", acceptedConnectors, getAcceptor().getAcceptedConnectors().length); //$NON-NLS-1$
      }

      TestSignalProtocol trustedProtocol = new TestSignalProtocol(trustedConnector);
      try
      {
        int value = 0x5A17;
        assertEquals(value, new IntRequest(trustedProtocol, value).send().intValue());
      }
      finally
      {
        trustedProtocol.close();
      }
    }
    finally
    {
      untrustedConnector.close();
    }
  }

  private static SSLContext createEmptyTrustContext() throws Exception
  {
    KeyStore trustStore = KeyStore.getInstance("JKS"); //$NON-NLS-1$
    trustStore.load(null, null);
    TrustManagerFactory trustManagers = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagers.init(trustStore);
    SSLContext sslContext = SSLContext.getInstance("TLS"); //$NON-NLS-1$
    sslContext.init(null, trustManagers.getTrustManagers(), new SecureRandom());
    return sslContext;
  }
}
