/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Caspar De Groot - maintenance
 */
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.spi.net4j.AcceptorFactory;

/**
 * @author Eike Stepper
 */
public class TCPAcceptorFactory extends AcceptorFactory
{
  public static final String TYPE = "tcp"; //$NON-NLS-1$

  private static final String SEPARATOR = ":"; //$NON-NLS-1$

  public TCPAcceptorFactory()
  {
    super(TYPE);
  }

  /**
   * Allows derived classes to override the TYPE identifier
   */
  protected TCPAcceptorFactory(String type)
  {
    super(type);
  }

  public TCPAcceptor create(String description)
  {
    String address = ITCPAcceptor.DEFAULT_ADDRESS;
    int port = ITCPAcceptor.DEFAULT_PORT;

    if (!StringUtil.isEmpty(description))
    {
      String[] tokens = description.split(SEPARATOR);
      if (!StringUtil.isEmpty(tokens[0]))
      {
        address = tokens[0];
      }

      if (tokens.length > 1 && !StringUtil.isEmpty(tokens[1]))
      {
        port = Integer.parseInt(tokens[1]);
      }
    }

    TCPAcceptor acceptor = createAcceptor();
    acceptor.setAddress(address);
    acceptor.setPort(port);
    return acceptor;
  }

  protected TCPAcceptor createAcceptor()
  {
    return new TCPAcceptor();
  }

  @Override
  public String getDescriptionFor(Object object)
  {
    if (object instanceof TCPAcceptor)
    {
      TCPAcceptor acceptor = (TCPAcceptor)object;
      return acceptor.getAddress() + SEPARATOR + acceptor.getPort();
    }

    return null;
  }
}
