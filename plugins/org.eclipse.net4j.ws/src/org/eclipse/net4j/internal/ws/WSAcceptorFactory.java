/*
 * Copyright (c) 2020, 2024-2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.AcceptorFactory;

import org.w3c.dom.Element;

/**
 * Creates provider-neutral WebSocket acceptors that receive connections from a server provider.
 *
 * @author Eike Stepper
 */
public class WSAcceptorFactory extends AcceptorFactory
{
  /**
   * Default acceptor name used when no name is specified.
   */
  public static final String DEFAULT_ACCEPTOR_NAME = "default"; //$NON-NLS-1$

  public WSAcceptorFactory()
  {
    this(WSUtil.FACTORY_TYPE);
  }

  protected WSAcceptorFactory(String type)
  {
    super(type);
  }

  @Override
  public IWSAcceptor create(String description)
  {
    WSAcceptor acceptor = new WSAcceptor(getType());
    acceptor.setName(StringUtil.isEmpty(description) ? DEFAULT_ACCEPTOR_NAME : description);
    return acceptor;
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof IWSAcceptor)
    {
      return ((IWSAcceptor)object).getName();
    }

    return null;
  }

  /**
   * Parses acceptor names from the WS acceptor configuration element.
   *
   * @author Eike Stepper
   */
  public static class DescriptionParserFactory extends AcceptorDescriptionParser.Factory implements AcceptorDescriptionParser
  {
    public DescriptionParserFactory()
    {
      this(WSUtil.FACTORY_TYPE);
    }

    protected DescriptionParserFactory(String type)
    {
      super(type);
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
