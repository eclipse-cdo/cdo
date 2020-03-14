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
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.jetty.Net4jWebSocket;

import org.eclipse.spi.net4j.Acceptor;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class WSAcceptor extends Acceptor implements IWSAcceptor
{
  public static final boolean DEFAULT_START_SYNCHRONOUSLY = true;

  public static final long DEFAULT_SYNCHRONOUS_START_TIMEOUT = Worker.DEFAULT_TIMEOUT;

  private String name;

  public WSAcceptor()
  {
  }

  @Override
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    checkInactive();
    this.name = name;
  }

  public WSServerConnector handleAccept(Net4jWebSocket webSocket)
  {
    WSServerConnector connector = createConnector();
    prepareConnector(connector);
    connector.setWebSocket(webSocket);
    connector.activate();
    addConnector(connector);
    return connector;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("WSAcceptor[{0}]", name); //$NON-NLS-1$
  }

  protected WSServerConnector createConnector()
  {
    return new WSServerConnector(this);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (StringUtil.isEmpty(name))
    {
      throw new IllegalStateException("name is null or empty"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    WSAcceptorManager.INSTANCE.registerAcceptor(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    WSAcceptorManager.INSTANCE.deregisterAcceptor(this);
    super.doDeactivate();
  }
}
