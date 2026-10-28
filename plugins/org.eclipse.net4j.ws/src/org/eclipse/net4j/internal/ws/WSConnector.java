/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationContext;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.IWSNegotiationContext;
import org.eclipse.net4j.ws.WSUtil;

import org.eclipse.spi.net4j.MessageConnector;

import java.net.HttpCookie;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * WebSocket connector configuration layered on Net4j's asynchronous message connector.
 * <p>
 * URI, acceptor route, transport type, and provider type are fixed for this connector product. The cookie list is copied
 * on construction and exposed as an unmodifiable list; its {@link HttpCookie} elements remain mutable, and callers of
 * {@link #getCookies()} receive those same element references.
 *
 * @author Eike Stepper
 */
public class WSConnector extends MessageConnector implements IWSConnector
{
  private final URI serviceURI;

  private final String acceptorName;

  private final String type;

  private final List<HttpCookie> cookies;

  private final String providerType;

  /**
   * Creates a connector using the default message-connection provider type.
   *
   * @param serviceURI base service URI
   * @param acceptorName target server acceptor route
   * @param type WS or WSS transport factory type
   * @param cookies cookies for this connector's handshake
   */
  public WSConnector(URI serviceURI, String acceptorName, String type, List<HttpCookie> cookies)
  {
    this(serviceURI, acceptorName, type, cookies, WSConnectorFactory.DEFAULT_PROVIDER_TYPE);
  }

  /**
   * Creates a connector with an explicitly selected message-connection provider.
   *
   * @param serviceURI base service URI
   * @param acceptorName target server acceptor route
   * @param type WS or WSS transport factory type
   * @param cookies cookies for this connector's handshake
   * @param providerType registered message-connection factory type
   */
  public WSConnector(URI serviceURI, String acceptorName, String type, List<HttpCookie> cookies, String providerType)
  {
    this.serviceURI = serviceURI;
    this.acceptorName = acceptorName;
    this.type = type;
    this.cookies = Collections.unmodifiableList(new ArrayList<>(cookies));
    this.providerType = providerType;
  }

  @Override
  public URI getServiceURI()
  {
    return serviceURI;
  }

  @Override
  public String getAcceptorName()
  {
    return acceptorName;
  }

  @Override
  public List<HttpCookie> getCookies()
  {
    return cookies;
  }

  @Override
  public String getProviderType()
  {
    return providerType;
  }

  @Override
  public void setMessageConnection(IMessageConnection connection)
  {
    setConnection(connection);
  }

  @Override
  public String getURL()
  {
    return type + "://" + WSUtil.getConnectorDescription(serviceURI, acceptorName); //$NON-NLS-1$
  }

  @Override
  protected INegotiationContext createNegotiationContext()
  {
    return new WSNegotiationContext(super.createNegotiationContext());
  }

  /**
   * Exposes the generic message negotiation behavior through the WS-specific API.
   *
   * @author Eike Stepper
   */
  private final class WSNegotiationContext extends NegotiationContext implements IWSNegotiationContext
  {
    private final INegotiationContext delegate;

    private WSNegotiationContext(INegotiationContext delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public IWSConnector getConnector()
    {
      return WSConnector.this;
    }

    @Override
    public ByteBuffer getBuffer()
    {
      return delegate.getBuffer();
    }

    @Override
    public void transmitBuffer(ByteBuffer buffer)
    {
      delegate.transmitBuffer(buffer);
    }

    @Override
    public Receiver getReceiver()
    {
      return delegate.getReceiver();
    }

    @Override
    public void setReceiver(Receiver receiver)
    {
      delegate.setReceiver(receiver);
    }

    @Override
    public Enum<?> getState()
    {
      return delegate.getState();
    }

    @Override
    public void setState(Enum<?> state)
    {
      delegate.setState(state);
    }

    @Override
    public void setUserID(String userID)
    {
      delegate.setUserID(userID);
    }

    @Override
    public Object getInfo()
    {
      return delegate.getInfo();
    }

    @Override
    public void setInfo(Object info)
    {
      delegate.setInfo(info);
    }

    @Override
    public void setFinished(boolean success)
    {
      delegate.setFinished(success);
    }

    @Override
    public Enum<?> waitUntilFinished(long timeout)
    {
      return delegate.waitUntilFinished(timeout);
    }
  }
}
