/*
 * Copyright (c) 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.ws.jetty.Net4jWebSocket;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class WSClientConnector extends WSConnector
{
  // private static final long CLIENT_IDLE_TIMEOUT =
  // OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.internal.ws.WSClientConnector.clientIdleTimeout", 30000);

  private WebSocketClient client;

  private boolean ownedClient;

  private long connectTimeout = 5000;

  private String url;

  private URI serviceURI;

  private String acceptorName;

  public WSClientConnector()
  {
  }

  @Override
  public Location getLocation()
  {
    return Location.CLIENT;
  }

  public WebSocketClient getClient()
  {
    return client;
  }

  public void setClient(WebSocketClient client)
  {
    checkInactive();
    this.client = client;
  }

  public long getConnectTimeout()
  {
    return connectTimeout;
  }

  public void setConnectTimeout(long connectTimeout)
  {
    checkInactive();
    this.connectTimeout = connectTimeout;
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
  public String getURL()
  {
    return url;
  }

  public void setURL(String url) throws URISyntaxException
  {
    checkInactive();
    this.url = url;

    URI uri = new URI(url);
    IPath path = new Path(uri.getPath());

    int index = getAcceptorSegmentIndex(path);
    if (index == -1)
    {
      throw new URISyntaxException(url, "Acceptor name prefix '" + ACCEPTOR_NAME_PREFIX + "' not found");
    }

    String serviceURIString = uri.getScheme() + "://" + uri.getAuthority(); //$NON-NLS-1$
    for (int i = 0; i < index; i++)
    {
      serviceURIString += "/" + path.segment(i); //$NON-NLS-1$
    }

    serviceURI = new URI(serviceURIString);
    acceptorName = path.segment(index).substring(ACCEPTOR_NAME_PREFIX.length());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("WSClientConnector[{0}]", getURL()); //$NON-NLS-1$
  }

  protected int getAcceptorSegmentIndex(IPath path)
  {
    for (int i = 0; i < path.segmentCount(); i++)
    {
      if (path.segment(i).startsWith(ACCEPTOR_NAME_PREFIX))
      {
        return i;
      }
    }

    return -1;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    if (client == null)
    {
      client = new WebSocketClient();
      ownedClient = true;
    }

    super.doBeforeActivate();

    if (serviceURI == null)
    {
      throw new IllegalStateException("serviceURI is null"); //$NON-NLS-1$
    }

    if (acceptorName == null || acceptorName.length() == 0)
    {
      throw new IllegalStateException("acceptorName is null or empty"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    if (ownedClient)
    {
      // TODO Find out how to set the timeouts with Jetty 10.
      // client.getPolicy().setIdleTimeout(CLIENT_IDLE_TIMEOUT);
      client.start();
    }

    super.doActivate();

    Net4jWebSocket webSocket = new Net4jWebSocket(this);
    setWebSocket(webSocket);

    ClientUpgradeRequest request = new ClientUpgradeRequest();
    request.setHeader(ACCEPTOR_NAME_HEADER, acceptorName);

    Future<Session> result = client.connect(webSocket, serviceURI, request);
    result.get(connectTimeout, TimeUnit.MILLISECONDS);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();

    if (ownedClient)
    {
      client.stop();
      client = null;
    }
  }
}
