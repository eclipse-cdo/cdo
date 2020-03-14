/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws.jetty;

import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

/**
 * @author Eike Stepper
 */
public class Net4jWebSocketServlet extends WebSocketServlet
{
  private static final long ASYNC_WRITE_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet.asyncWriteTimeout", 30000);

  private static final long IDLE_TIMEOUT = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.ws.jetty.Net4jWebSocketServlet.idleTimeout", 30000);

  private static final long serialVersionUID = 1L;

  public Net4jWebSocketServlet()
  {
  }

  @Override
  public void configure(WebSocketServletFactory factory)
  {
    factory.getPolicy().setAsyncWriteTimeout(ASYNC_WRITE_TIMEOUT);
    factory.getPolicy().setIdleTimeout(IDLE_TIMEOUT);
    factory.register(Net4jWebSocket.class);
  }
}
