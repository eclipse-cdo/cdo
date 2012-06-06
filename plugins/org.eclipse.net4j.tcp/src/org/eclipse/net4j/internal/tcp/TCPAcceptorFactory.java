/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
    Data data = new Data(description);

    TCPAcceptor acceptor = createAcceptor();
    acceptor.setAddress(data.getAddress());
    acceptor.setPort(data.getPort());
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

  /**
   * @author Eike Stepper
   */
  public static class Data
  {
    private String address = ITCPAcceptor.DEFAULT_ADDRESS;

    private int port = ITCPAcceptor.DEFAULT_PORT;

    public Data()
    {
    }

    public Data(String address, int port)
    {
      this.address = address;
      this.port = port;
    }

    public Data(String description)
    {
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
    }

    public String getAddress()
    {
      return address == null ? "" : address;
    }

    public int getPort()
    {
      return port;
    }

    @Override
    public String toString()
    {
      return address + SEPARATOR + port;
    }
  }
}
