/*
 * Copyright (c) 2024-2026 Obeo and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.ws.WSSUtil;

import org.w3c.dom.Element;

/**
 * Creates provider-neutral WSS acceptors.
 *
 * @author Eike Stepper
 */
public class WSSAcceptorFactory extends WSAcceptorFactory
{
  public WSSAcceptorFactory()
  {
    super(WSSUtil.FACTORY_TYPE);
  }

  /**
   * Parses acceptor names from the WSS acceptor configuration element.
   *
   * @author Eike Stepper
   */
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
