/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.doc.operators;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSUtil;

import java.net.URI;

/**
 * Operating and Troubleshooting WebSockets
 * <p>
 * This article focuses on day-to-day ownership, shutdown, and diagnosis of Net4j WS/WSS connections. For Jetty deployment
 * and TLS setup, see the related Operator's Guide articles linked below.
 * <p>
 * <b>Table of Contents</b> {@toc}
 *
 * @author Eike Stepper
 */
public class Doc04_OperatingAndTroubleshootingWebSockets
{
  /**
   * Resource Ownership and Lifecycle
   * <p>
   * An <code>IWSConnector</code> is an individual Net4j client connection and can be closed on its own. A managed container
   * owns the products it creates, including factories, acceptors, and connectors; deactivate the container only when its
   * owner is shutting down that container. A shared application container must remain active when one connector closes.
   * <p>
   * The application owns any custom JDK <code>HttpClient</code> supplied to a provider and must keep it alive while the
   * provider's connectors use it. Java 11 has no <code>HttpClient.close()</code>; Java 21 adds client lifecycle methods.
   * Stop its Net4j connectors before using those newer methods. The application that embeds Jetty owns and stops its
   * <code>Server</code> and listeners.
   * {@link #closeOneConnector() CloseOneConnector.java}
   */
  public class Resource_Ownership_and_Lifecycle
  {
    /**
     * Closes one connector while a second connector continues to use the shared container.
     *
     * @snip
     */
    public void closeOneConnector()
    {
      IManagedContainer sharedContainer = ContainerUtil.createInitializedContainer();
      IWSConnector connectorToClose = null;
      IWSConnector remainingConnector = null;

      try
      {
        connectorToClose = WSUtil.getConnector(sharedContainer, URI.create("ws://gateway.example.net/net4j"), "public");
        remainingConnector = WSUtil.getConnector(sharedContainer, URI.create("ws://backup.example.net/net4j"), "public");

        connectorToClose.connect(5_000);
        remainingConnector.connect(5_000);

        connectorToClose.close();
        connectorToClose = null;
        // The remaining connector still uses the shared container after the first connector closes.
      }
      finally
      {
        try
        {
          if (connectorToClose != null)
          {
            connectorToClose.close();
          }
        }
        finally
        {
          try
          {
            if (remainingConnector != null)
            {
              remainingConnector.close();
            }
          }
          finally
          {
            // This snippet owns the example container; a real shared-container owner does this at application shutdown.
            LifecycleUtil.deactivate(sharedContainer);
          }
        }
      }
    }
  }

  /**
   * Graceful Shutdown
   * <p>
   * Stop producing new application messages first. Close or drain Net4j protocols and connectors, stop admitting new
   * HTTP upgrades, then stop the application-owned Jetty server. Deactivate the acceptor and its container after the server
   * no longer accepts upgrades. For a container shared with other transports, close only the WebSocket products owned by
   * the stopping component; let the container owner choose when the complete container stops.
   * <p>
   * Cleanup should use nested <code>finally</code> blocks so an exception while stopping one component does not skip the
   * remaining resource cleanup. Keep HTTP client shutdown after the connectors that borrow the client have stopped.
   * {@link Doc01_DeployingWebSocketServers}
   */
  public class Graceful_Shutdown
  {
  }

  /**
   * Diagnosing Connection and Upgrade Failures
   * <p>
   * First verify that the WS core bundle is resolved and that the container contains the provider type named in the
   * connector description. A missing custom provider is reported as an error identifying the message-connection provider
   * type. Next check the endpoint scheme, route name, service path, and whether the expected acceptor is active.
   * <p>
   * Jetty routing requires the decoded path suffix and the <code>Net4jAcceptor</code> header to name the same acceptor.
   * An unknown, inactive, or mismatched route returns HTTP 404. A request whose secure state does not match its WS/WSS
   * acceptor returns HTTP 403. Check HTTPS termination and trusted forwarding configuration when WSS gets a 403.
   * <p>
   * For JDK client failures, inspect the complete exception cause chain from connector activation: it can distinguish a
   * provider lookup error, certificate validation error, proxy connection failure, and HTTP upgrade rejection. Do not log
   * complete connector descriptions when they carry cookies.
   */
  public class Diagnosing_Connection_and_Upgrade_Failures
  {
  }

  /**
   * TLS and Proxy Diagnosis
   * <p>
   * For a certificate error, verify the service host against the certificate name, confirm the server sends its
   * intermediates, check expiry, and inspect the trust store used by the selected provider. For a proxy failure, verify
   * the configured <code>ProxySelector</code>, proxy reachability, and whether the <code>Authenticator</code> receives a
   * proxy or origin challenge. Basic authentication over HTTP CONNECT may be disabled by the JDK unless its JVM-wide
   * disabled-schemes setting is changed; assess the security implications before changing that setting.
   * <p>
   * The Net4j WS and Jetty adapter bundles declare <code>debug</code> tracing options, but the current WS implementation
   * does not emit transport events through those tracers. Do not expect enabling those options to produce handshake
   * diagnostics. Use the connector exception cause chain and the application's configured Jetty/JDK logging facilities.
   */
  public class TLS_and_Proxy_Diagnosis
  {
  }

  /**
   * Operational Checklist
   * <p>
   * Before release, verify the deployed WS feature and optional Jetty adapter version; confirm that every client provider
   * is registered in the connector's container; keep provider types distinct when clients have different trust or proxy
   * rules; and test each acceptor name over the intended scheme. Confirm certificate renewal and trust-store deployment,
   * cookie expiry and secrecy, proxy credential rotation, bounded connection timeouts, and coordinated shutdown.
   * <p>
   * During an incident, record the endpoint scheme and host, acceptor name, provider type, connector state, status code,
   * and exception causes. Redact cookie values, credentials, and full descriptions before storing diagnostics.
   */
  public class Operational_Checklist
  {
  }
}
