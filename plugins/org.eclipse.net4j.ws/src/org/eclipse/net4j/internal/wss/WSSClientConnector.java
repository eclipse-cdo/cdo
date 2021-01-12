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

import org.eclipse.net4j.internal.ws.WSClientConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.wss.WSSUtil;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.transport.HttpClientTransportDynamic;
import org.eclipse.jetty.ee8.websocket.client.WebSocketClient;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import java.io.File;
import java.net.URI;
import java.text.MessageFormat;

/**
 * @author mporhel
 */
public class WSSClientConnector extends WSClientConnector
{

  @Override
  public String toString()
  {
    return MessageFormat.format("WSSClientConnector[{0}]", getURL()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {

    if (getServiceURI().getScheme().equals(WSSUtil.FACTORY_TYPE))
    {
      SslContextFactory.Client sslContextFactory = createSslContextFactory();

      ClientConnector clientConnector = new ClientConnector();
      clientConnector.setSslContextFactory(sslContextFactory);

      HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));

      WebSocketClient securedClient = new WebSocketClient(httpClient);
      setClient(securedClient);

      // Let WSClientConnector manage the WebSocketClient, see WSClientConnector::doActivate/doDeactivate
      ownedClient = true;

      super.doBeforeActivate();
    }
    else
    {
      throw new IllegalArgumentException("Service Uri should have wss:// scheme");
    }

  }

  private SslContextFactory.Client createSslContextFactory()
  {
    // initialize SSL Context
    SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();

    boolean trustAll = Boolean.getBoolean("org.eclipse.net4j.internal.wss.ssl.trustall");
    String eia = System.getProperty("org.eclipse.net4j.internal.wss.ssl.endpointIdentificationAlgorithm");

    String passphrase = System.getProperty("org.eclipse.net4j.internal.wss.ssl.passphrase");
    String trusturi = System.getProperty("org.eclipse.net4j.internal.wss.ssl.trust");
    String trustType = System.getProperty("org.eclipse.net4j.internal.wss.ssl.trust.type");
    String trustAlg = System.getProperty("org.eclipse.net4j.internal.wss.ssl.trust.manager.factory.algorithm");

    sslContextFactory.setTrustAll(trustAll);

    if ("null".equals(eia))
    {
      sslContextFactory.setEndpointIdentificationAlgorithm(null);
    }
    else if (!StringUtil.isEmpty(eia))
    {
      sslContextFactory.setEndpointIdentificationAlgorithm(eia);
    }

    if (trusturi != null)
    {
      URI uri = URI.create(trusturi);
      File file = new File(uri);
      if (file.exists())
      {
        sslContextFactory.setTrustStoreResource(new PathResourceFactory().newResource(uri));
        sslContextFactory.setTrustStorePassword(passphrase);
        if (!StringUtil.isEmpty(trustType))
        {
          sslContextFactory.setTrustStoreType(trustType);
        }

        if (!StringUtil.isEmpty(trustAlg))
        {
          sslContextFactory.setTrustManagerFactoryAlgorithm(trustAlg);
        }
      }
    }
    return sslContextFactory;
  }

}
