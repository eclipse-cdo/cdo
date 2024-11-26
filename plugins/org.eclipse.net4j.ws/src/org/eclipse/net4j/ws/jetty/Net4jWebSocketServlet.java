/*
 * Copyright (c) 2020, 2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws.jetty;

import org.eclipse.jetty.ee8.websocket.server.JettyWebSocketServlet;
import org.eclipse.jetty.ee8.websocket.server.JettyWebSocketServletFactory;

/**
 * @author Eike Stepper
 */
public class Net4jWebSocketServlet extends JettyWebSocketServlet
{
  // private static final long ASYNC_WRITE_TIMEOUT =
  // OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet.asyncWriteTimeout", 30000);

  // private static final long IDLE_TIMEOUT =
  // OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet.idleTimeout", 30000);

  private static final long serialVersionUID = 1L;

  public Net4jWebSocketServlet()
  {
  }

  @Override
  public void configure(JettyWebSocketServletFactory factory)
  {
    // TODO Find out how to set the timeouts with Jetty 10.
    // factory.getPolicy().setAsyncWriteTimeout(ASYNC_WRITE_TIMEOUT);
    // factory.getPolicy().setIdleTimeout(IDLE_TIMEOUT);
    factory.register(Net4jWebSocket.class);
  }
}
