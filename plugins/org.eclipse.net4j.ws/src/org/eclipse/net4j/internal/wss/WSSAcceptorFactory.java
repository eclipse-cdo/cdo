/*
 * Copyright (c) 2024 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Maxime Porhel (Obeo) - initial API and implementation
 */
package org.eclipse.net4j.internal.wss;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.internal.ws.WSAcceptor;
import org.eclipse.net4j.internal.ws.WSAcceptorFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.wss.WSSUtil;

import org.w3c.dom.Element;

/**
 * @author mporhel
 */
public class WSSAcceptorFactory extends WSAcceptorFactory
{
  public WSSAcceptorFactory()
  {
    super(WSSUtil.FACTORY_TYPE);
  }

  /**
   * Allows derived classes to override the TYPE identifier
   */
  protected WSSAcceptorFactory(String type)
  {
    super(type);
  }

  @Override
  protected WSAcceptor createAcceptor()
  {
    WSAcceptor wsAcceptor = new WSAcceptor();
    wsAcceptor.setType(WSSUtil.FACTORY_TYPE);
    return wsAcceptor;
  }

  public static class DescriptionParserFactory extends AcceptorDescriptionParser.Factory implements AcceptorDescriptionParser
  {
    public DescriptionParserFactory()
    {
      super(WSSUtil.FACTORY_TYPE);
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
