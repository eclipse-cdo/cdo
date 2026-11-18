/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.examples.plain.server;

import org.eclipse.emf.cdo.spi.server.AbstractAppExtension;
import org.eclipse.emf.cdo.spi.server.IAppExtension4;
import org.eclipse.emf.cdo.spi.server.IAppExtension5;
import org.eclipse.emf.cdo.spi.server.IAppExtension6;

import org.eclipse.net4j.util.XMLConfiguration;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.File;

/**
 * Hosts a WebSocket acceptor selected by the prepared server XML configuration.
 *
 * <p>The Net4j acceptor is created by the normal network app extension. This extension then attaches the
 * existing Jetty WebSocket adapter to an HTTP listener and stops the Jetty server during CDO shutdown.
 * It starts no listener when the prepared configuration selected TCP or SSL. WSS requires an external
 * keystore selected through the standard JSSE properties; the client must trust the matching certificate.
 *
 * @author Eike Stepper
 */
public final class ConfiguredWebSocketServerExtension extends AbstractAppExtension implements IAppExtension4, IAppExtension5, IAppExtension6
{
  private Server server;

  public ConfiguredWebSocketServerExtension()
  {
  }

  @Override
  public String getName()
  {
    return "Configured WebSocket Server"; //$NON-NLS-1$
  }

  @Override
  public int getPriority()
  {
    return IAppExtension4.PRIORITY_DEFAULT;
  }

  @Override
  public boolean startBeforeRepositories()
  {
    return false;
  }

  @Override
  public void start(File configFile) throws Exception
  {
  }

  @Override
  public void start(XMLConfiguration configuration) throws Exception
  {
    NodeList acceptors = configuration.getDocument().getDocumentElement().getElementsByTagName("acceptor"); //$NON-NLS-1$

    for (int i = 0; i < acceptors.getLength(); i++)
    {
      Element element = (Element)acceptors.item(i);

      String type = element.getAttribute("type"); //$NON-NLS-1$
      if (WSUtil.FACTORY_TYPE.equals(type) || WSSUtil.FACTORY_TYPE.equals(type))
      {
        startServer(type, element.getAttribute("name")); //$NON-NLS-1$
        return;
      }
    }
  }

  private void startServer(String type, String acceptorName) throws Exception
  {
    IWSAcceptor acceptor = WSUtil.FACTORY_TYPE.equals(type) //
        ? WSUtil.getAcceptor(getContainer(), acceptorName) //
        : WSSUtil.getAcceptor(getContainer(), acceptorName);

    server = new Server();

    ContextHandler context = new ContextHandler("/"); //$NON-NLS-1$
    context.setHandler(JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor)); //$NON-NLS-1$
    server.setHandler(context);

    if (WSSUtil.FACTORY_TYPE.equals(type))
    {
      String keyStore = System.getProperty("javax.net.ssl.keyStore"); //$NON-NLS-1$
      if (keyStore == null || keyStore.isBlank())
      {
        throw new IllegalStateException("WSS requires a keystore configured with javax.net.ssl.keyStore"); //$NON-NLS-1$
      }

      SslContextFactory.Server ssl = new SslContextFactory.Server();
      ssl.setKeyStorePath(keyStore);
      ssl.setKeyStorePassword(System.getProperty("javax.net.ssl.keyStorePassword")); //$NON-NLS-1$
      ssl.setKeyStoreType(System.getProperty("javax.net.ssl.keyStoreType", "PKCS12")); //$NON-NLS-1$ //$NON-NLS-2$

      server.addConnector(new ServerConnector(server, //
          new SslConnectionFactory(ssl, HttpVersion.HTTP_1_1.asString()), //
          new HttpConnectionFactory(new HttpConfiguration())));
    }
    else
    {
      server.addConnector(new ServerConnector(server));
    }

    String host = System.getProperty("cdo.ws.host", "0.0.0.0"); //$NON-NLS-1$ //$NON-NLS-2$
    String port = System.getProperty("cdo.ws.port"); //$NON-NLS-1$

    ServerConnector connector = (ServerConnector)server.getConnectors()[0];
    connector.setHost(host);
    connector.setPort(port == null || port.isBlank() ? WSSUtil.FACTORY_TYPE.equals(type) ? 8443 : 8080 : Integer.parseInt(port));

    server.start();
  }

  @Override
  public void stop() throws Exception
  {
    if (server != null)
    {
      server.stop();
      server.destroy();
      server = null;
    }
  }
}
