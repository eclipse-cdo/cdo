/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.tests.config.IConstants;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;
import org.eclipse.emf.cdo.tests.config.impl.Scenario;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Verifies that WebSocket session configurations remain serializable as part of a test scenario.
 *
 * @author Eike Stepper
 */
public class WebSocketSessionConfigSerializationTest extends PlainTest
{
  public void testWSSessionConfigSerialization() throws Exception
  {
    Scenario scenario = roundTrip(IConstants.WS);
    assertTrue(scenario.getSessionConfig() instanceof SessionConfig.Net4j.AbstractWebSocket.WS);
  }

  public void testWSSSSessionConfigSerialization() throws Exception
  {
    Scenario scenario = roundTrip(IConstants.WSS);
    assertTrue(scenario.getSessionConfig() instanceof SessionConfig.Net4j.AbstractWebSocket.WSS);
  }

  private Scenario roundTrip(ISessionConfig sessionConfig) throws Exception
  {
    Scenario scenario = new Scenario(IConstants.MEM, sessionConfig, IConstants.NATIVE);
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    try (ObjectOutputStream output = new ObjectOutputStream(bytes))
    {
      output.writeObject(scenario);
    }

    try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray())))
    {
      return (Scenario)input.readObject();
    }
  }
}
