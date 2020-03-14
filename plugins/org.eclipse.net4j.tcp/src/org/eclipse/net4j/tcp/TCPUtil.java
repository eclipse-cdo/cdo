/*
 * Copyright (c) 2007, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelectorInjector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A utility class with static convenience methods.
 *
 * @author Eike Stepper
 */
public final class TCPUtil
{
  /**
   * @since 4.1
   */
  public static final String FACTORY_TYPE = "tcp"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String PORT_SEPARATOR = ":"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String CREDENTIALS_SEPARATOR = "@";

  private TCPUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new TCPSelectorFactory());
    container.registerFactory(new TCPAcceptorFactory());
    container.registerFactory(new TCPConnectorFactory());
    container.addPostProcessor(new TCPSelectorInjector());
  }

  public static ITCPAcceptor getAcceptor(IManagedContainer container, String description)
  {
    return (ITCPAcceptor)container.getElement(TCPAcceptorFactory.PRODUCT_GROUP, FACTORY_TYPE, description);
  }

  public static ITCPConnector getConnector(IManagedContainer container, String description)
  {
    return (ITCPConnector)container.getElement(TCPConnectorFactory.PRODUCT_GROUP, FACTORY_TYPE, description);
  }

  /**
   * Encapsulates the data needed to configure a {@link ITCPAcceptor TCP acceptor}.
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class AcceptorData
  {
    private static final int NO_PORT = -1;

    private String address = ITCPAcceptor.DEFAULT_ADDRESS;

    private int port = NO_PORT;

    public AcceptorData()
    {
    }

    public AcceptorData(String address, int port)
    {
      this.address = address;
      this.port = port;
    }

    public AcceptorData(String description)
    {
      if (!StringUtil.isEmpty(description))
      {
        String[] tokens = description.split(PORT_SEPARATOR);
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

    public AcceptorData(ITCPAcceptor acceptor)
    {
      this(acceptor.getAddress(), acceptor.getPort());
    }

    public String getAddress()
    {
      return address;
    }

    public int getPort()
    {
      if (port == NO_PORT)
      {
        return ITCPAcceptor.DEFAULT_PORT;
      }

      return port;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (address == null ? 0 : address.hashCode());
      result = prime * result + port;
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      AcceptorData other = (AcceptorData)obj;
      if (address == null)
      {
        if (other.address != null)
        {
          return false;
        }
      }
      else if (!address.equals(other.address))
      {
        return false;
      }

      if (port != other.port)
      {
        return false;
      }

      return true;
    }

    @Override
    public String toString()
    {
      String result = StringUtil.isEmpty(address) ? ITCPAcceptor.DEFAULT_ADDRESS : address;

      if (port != NO_PORT)
      {
        result = result + PORT_SEPARATOR + port;
      }

      return result;
    }
  }

  /**
   * Encapsulates the data needed to configure a {@link ITCPConnector TCP connector}.
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class ConnectorData
  {
    private static final int NO_PORT = -1;

    private String host;

    private int port = NO_PORT;

    private String userID;

    public ConnectorData()
    {
    }

    public ConnectorData(String host, int port, String userID)
    {
      this.host = host;
      this.port = port;
      this.userID = userID;
    }

    public ConnectorData(String description)
    {
      if (!StringUtil.isEmpty(description))
      {
        try
        {
          URI uri = parse(description);
          userID = uri.getUserInfo();
          host = uri.getHost();
          port = uri.getPort();
        }
        catch (URISyntaxException ex)
        {
          throw new ProductCreationException(ex);
        }
      }
    }

    public ConnectorData(ITCPConnector connector)
    {
      this(connector.getHost(), connector.getPort(), connector.getUserID());
    }

    public String getHost()
    {
      return host;
    }

    public int getPort()
    {
      if (port == NO_PORT)
      {
        return ITCPConnector.DEFAULT_PORT;
      }

      return port;
    }

    public String getUserID()
    {
      return userID;
    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      result = prime * result + (host == null ? 0 : host.hashCode());
      result = prime * result + port;
      result = prime * result + (userID == null ? 0 : userID.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
      {
        return true;
      }

      if (obj == null)
      {
        return false;
      }

      if (getClass() != obj.getClass())
      {
        return false;
      }

      ConnectorData other = (ConnectorData)obj;
      if (host == null)
      {
        if (other.host != null)
        {
          return false;
        }
      }
      else if (!host.equals(other.host))
      {
        return false;
      }

      if (port != other.port)
      {
        return false;
      }

      if (userID == null)
      {
        if (other.userID != null)
        {
          return false;
        }
      }
      else if (!userID.equals(other.userID))
      {
        return false;
      }

      return true;
    }

    @Override
    public String toString()
    {
      String result = StringUtil.safe(host);
      if (userID != null)
      {
        result = userID + CREDENTIALS_SEPARATOR + result;
      }

      if (port != NO_PORT)
      {
        result = result + PORT_SEPARATOR + port;
      }

      return result;
    }

    private static URI parse(String description) throws URISyntaxException
    {
      return new URI("tcp://" + description);
    }
  }
}
