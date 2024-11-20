/*
 * Copyright (c) 2020, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Maxime Porhel (Obeo) - WSS Support
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.ws.jetty.Net4jWebSocket;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jetty.client.BasicAuthentication;
import org.eclipse.jetty.client.BasicAuthentication.BasicResult;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.Origin.Address;
import org.eclipse.jetty.client.ProxyConfiguration.Proxy;
import org.eclipse.jetty.ee8.websocket.api.Session;
import org.eclipse.jetty.ee8.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.ee8.websocket.client.WebSocketClient;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public class WSClientConnector extends WSConnector
{
  // private static final long CLIENT_IDLE_TIMEOUT =
  // OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.internal.ws.WSClientConnector.clientIdleTimeout", 30000);
  private static final String CLIENT_BASIC_AUTH = "org.eclipse.net4j.internal.ws.WSClientConnector.clientBasicAuth";

  private WebSocketClient client;

  protected boolean ownedClient;

  private long connectTimeout = 5000;

  private String url;

  private URI serviceURI;

  private String acceptorName;

  private final List<HttpCookie> cookies;

  public WSClientConnector()
  {
    cookies = new ArrayList<>();
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

  /**
   * Compute and set the service uri and the acceptor name from he provided url.
   * Must be called before activation (before the calls to doBeforeActivate() and doActivate()).
   */
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

  /**
   * Clear known cookies and use the new ones.
   */
  public void setCookies(List<HttpCookie> httpCookies)
  {
    cookies.clear();
    if (httpCookies != null && !httpCookies.isEmpty())
    {
      cookies.addAll(httpCookies);
    }
  }

  /**
   * Return an unmodifiable list of cookies.
   */
  public List<HttpCookie> getCookies()
  {
    return Collections.unmodifiableList(cookies);
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

  /**
   * Configures the proxy if necessary, according to org.eclipse.core.net settings.
   * @param securedClient  the WebSocketClient to configure
   */
  protected void configureProxy(WebSocketClient client, String proxyType)
  {
    Optional<IProxyService> service = getProxyService();
    if (service.isPresent())
    {
      if (service.get().isProxiesEnabled() && !StringUtil.isEmpty(proxyType))
      {
        IProxyData proxyData = service.get().getProxyData(proxyType);
        if (proxyData != null && proxyData.getHost() != null)
        {
          Address address = new Address(proxyData.getHost(), proxyData.getPort());
          Proxy proxy = new HttpProxy(address, false);
          if (proxyData.isRequiresAuthentication())
          {
            // configure auth if necessary
            BasicAuthentication auth = new BasicAuthentication(proxy.getURI(), "<<ANY_REALM>>", //$NON-NLS-1$
                proxyData.getUserId(), proxyData.getPassword());
            client.getHttpClient().getAuthenticationStore().addAuthentication(auth);
            client.getHttpClient().getAuthenticationStore()
                .addAuthenticationResult(new BasicAuthentication.BasicResult(proxy.getURI(), proxyData.getUserId(), proxyData.getPassword()));
          }
          client.getHttpClient().getProxyConfiguration().getProxies().add(proxy);
        }
      }
    }
  }

  private Optional<IProxyService> getProxyService()
  {
    IProxyService service = null;
    Bundle bundle = FrameworkUtil.getBundle(org.eclipse.core.runtime.Plugin.class);
    BundleContext bundleContext = bundle != null ? bundle.getBundleContext() : null;
    if (bundleContext != null)
    {
      ServiceTracker<IProxyService, IProxyService> tracker = new ServiceTracker<>(bundleContext, IProxyService.class, null);
      tracker.open();
      service = tracker.getService();
      tracker.close();
    }
    return Optional.ofNullable(service);
  }

  protected void configureBasicAuthentication(WebSocketClient client)
  {
    // If the Net4jWebsocketServlet is deployed on path for which the Jetty instance requires basic authentication.
    String property = OMPlatform.INSTANCE.getProperty(CLIENT_BASIC_AUTH + ".login");
    if (!StringUtil.isEmpty(property))
    {
      // BasicAuthentication auth = new BasicAuthentication(serviceURI, "<<ANY_REALM>>", login, mdp);
      // client.getHttpClient().getAuthenticationStore().addAuthentication(auth);
      BasicResult basicResult = new BasicAuthentication.BasicResult(serviceURI, property, OMPlatform.INSTANCE.getProperty(CLIENT_BASIC_AUTH + ".password", ""));
      client.getHttpClient().getAuthenticationStore().addAuthenticationResult(basicResult);
    }

  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    // setURL must be called during cr
    if (serviceURI == null)
    {
      throw new IllegalStateException("serviceURI is null"); //$NON-NLS-1$
    }

    if (acceptorName == null || acceptorName.length() == 0)
    {
      throw new IllegalStateException("acceptorName is null or empty"); //$NON-NLS-1$
    }

    if (client == null)
    {
      client = new WebSocketClient();
      configureProxy(client, IProxyData.HTTP_PROXY_TYPE);
      configureBasicAuthentication(client);
      ownedClient = true;
    }

    super.doBeforeActivate();

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

    if (cookies != null)
    {
      for (HttpCookie cookie : cookies)
      {
        request.getCookies().add(cookie);
      }
    }

    try
    {
      Future<Session> result = client.connect(webSocket, serviceURI, request);
      result.get(connectTimeout, TimeUnit.MILLISECONDS);
    }
    catch (Exception e)
    {
      if (ownedClient && client != null)
      {
        client.stop();
        client = null;
      }
      throw e;
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    super.doDeactivate();

    if (ownedClient && client != null)
    {
      client.stop();
      client = null;
    }
  }
}
