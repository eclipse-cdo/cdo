/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Verifies that CDO test teardown closes every active WebSocket connection.
 *
 * @author Eike Stepper
 */
public class WebSocketTeardownTest extends AbstractCDOTest
{
  private final List<IConnector> connectors = new ArrayList<>();

  @Requires(ISessionConfig.CAPABILITY_NET4J_WS)
  public void testWSClosesMultipleConnectors() throws Exception
  {
    openMultipleConnectors(false);
  }

  @Requires(ISessionConfig.CAPABILITY_NET4J_WSS)
  public void testWSSClosesMultipleConnectors() throws Exception
  {
    openMultipleConnectors(true);
  }

  @Override
  protected void doTearDown() throws Exception
  {
    try
    {
      super.doTearDown();
    }
    finally
    {
      for (IConnector connector : connectors)
      {
        assertEquals("Connector remained active after CDO fixture teardown", LifecycleState.INACTIVE, LifecycleUtil.getLifecycleState(connector));
      }
    }
  }

  private void openMultipleConnectors(boolean secure) throws Exception
  {
    SessionConfig.Net4j sessionConfig = (SessionConfig.Net4j)getSessionConfig();
    IConnector configuredConnector = sessionConfig.getConnector();
    assertTrue("Configured connector is not a WebSocket connector", configuredConnector instanceof IWSConnector);
    IWSConnector configuredWSConnector = (IWSConnector)configuredConnector;

    if (secure)
    {
      connectors.add(WSSUtil.getConnector(sessionConfig.getClientContainer(), configuredWSConnector.getServiceURI(), configuredWSConnector.getAcceptorName(),
          "provider:" + configuredWSConnector.getProviderType(), "cookie:teardown=secondary"));
    }
    else
    {
      connectors.add(WSUtil.getConnector(sessionConfig.getClientContainer(), configuredWSConnector.getServiceURI(), configuredWSConnector.getAcceptorName(),
          "cookie:teardown=secondary"));
    }

    connectors.add(configuredConnector);
    assertNotSame("Connector descriptions must produce separate client connections", connectors.get(0), connectors.get(1));

    LifecycleUtil.activate(connectors.get(0));
    LifecycleUtil.activate(connectors.get(1));
    assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connectors.get(0)));
    assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connectors.get(1)));

    IWSAcceptor acceptor = (IWSAcceptor)sessionConfig.getAcceptor();
    List<IConnector> acceptedConnectors = new ArrayList<>(Arrays.asList(acceptor.getAcceptedConnectors()));
    assertEquals("Expected an accepted server connector for each client connector", 2, acceptedConnectors.size());
    connectors.addAll(acceptedConnectors);

    for (IConnector connector : acceptedConnectors)
    {
      assertEquals(LifecycleState.ACTIVE, LifecycleUtil.getLifecycleState(connector));
    }
  }
}
