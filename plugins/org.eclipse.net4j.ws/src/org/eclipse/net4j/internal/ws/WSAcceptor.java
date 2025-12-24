/*
 * Copyright (c) 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.Net4jWebSocket;

import org.eclipse.spi.net4j.Acceptor;

import org.w3c.dom.Element;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class WSAcceptor extends Acceptor implements IWSAcceptor
{
  public static final boolean DEFAULT_START_SYNCHRONOUSLY = true;

  public static final long DEFAULT_SYNCHRONOUS_START_TIMEOUT = Worker.DEFAULT_TIMEOUT;

  private String name;

  private String type = WSUtil.FACTORY_TYPE;

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

  public void setType(String type)
  {
    checkInactive();
    this.type = type;
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
    if (!WSUtil.FACTORY_TYPE.equalsIgnoreCase(type))
    {
      return MessageFormat.format("WSAcceptor[{0}, {1}]", type, name); //$NON-NLS-1$
    }
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

  /**
   * @author Eike Stepper
   */
  public static class DescriptionParserFactory extends AcceptorDescriptionParser.Factory implements AcceptorDescriptionParser
  {
    public DescriptionParserFactory()
    {
      super(WSUtil.FACTORY_TYPE);
    }

    @Override
    public AcceptorDescriptionParser create(String description) throws ProductCreationException
    {
      return this;
    }

    @Override
    public String getAcceptorDescription(Element acceptorConfig)
    {
      return acceptorConfig.getAttribute("name"); //$NON-NLS-1$
    }
  }
}
