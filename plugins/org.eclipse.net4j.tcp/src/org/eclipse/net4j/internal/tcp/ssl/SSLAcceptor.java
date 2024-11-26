/*
 * Copyright (c) 2011, 2012, 2020, 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - initial API and implementation
 *    Caspar De Groot (No Magic Asia Ltd.) - initial API and implementation
 */
package org.eclipse.net4j.internal.tcp.ssl;

import org.eclipse.net4j.TransportConfigurator.AcceptorDescriptionParser;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.w3c.dom.Element;

import java.text.MessageFormat;

/**
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 * @author Caspar De Groot (No Magic Asia Ltd.)
 * @since 4.0
 */
public class SSLAcceptor extends TCPAcceptor
{
  @Override
  public boolean needsBufferProvider()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("SSLAcceptor[{0}:{1}]", getAddress(), getPort()); //$NON-NLS-1$
  }

  @Override
  protected TCPConnector createConnector()
  {
    return new SSLServerConnector(this);
  }

  /**
   * @author Eike Stepper
   */
  public static class DescriptionParserFactory extends AcceptorDescriptionParser.Factory implements AcceptorDescriptionParser
  {
    public DescriptionParserFactory()
    {
      super(SSLAcceptorFactory.TYPE);
    }

    @Override
    public AcceptorDescriptionParser create(String description) throws ProductCreationException
    {
      return this;
    }

    @Override
    public String getAcceptorDescription(Element acceptorConfig)
    {
      String listenAddr = acceptorConfig.getAttribute("listenAddr"); //$NON-NLS-1$
      String port = acceptorConfig.getAttribute("port"); //$NON-NLS-1$
      return (listenAddr == null ? "" : listenAddr) + (StringUtil.isEmpty(port) ? "" : ":" + port); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }
}
