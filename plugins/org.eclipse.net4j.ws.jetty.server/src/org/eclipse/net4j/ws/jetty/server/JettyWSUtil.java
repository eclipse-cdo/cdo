/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.ws.jetty.server;

import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSAcceptorManager;
import org.eclipse.net4j.ws.IWSConnector;
import org.eclipse.net4j.ws.WSSUtil;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.internal.server.JettyWebSocketMessageConnection;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.ServerWebSocketContainer;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a Jetty WebSocket upgrade handler scoped to explicitly configured active Net4j WS acceptors.
 * <p>
 * The embedding application owns and configures the Jetty server, its network listeners, and handler tree. This utility contributes an
 * upgrade handler; it does not create, start, stop, or own those server resources. The acceptor list scopes routing for this handler,
 * preventing it from selecting another context's acceptor merely because that acceptor is registered in the JVM-wide Net4j registry.
 * Other provider integrations may use different routing arrangements.
 *
 * @author Eike Stepper
 */
public final class JettyWSUtil
{
  private JettyWSUtil()
  {
  }

  /**
   * Creates a Jetty handler that registers one WebSocket path and routes upgrades only to the configured Net4j acceptors. Both the
   * decoded request path suffix (the {@code @name} route) and {@code Net4jAcceptor} handshake header must identify the same acceptor.
   * At routing time, the selected acceptor must still be the instance registered under its name. Registry removal or replacement is
   * therefore observed by subsequent routing checks. A request that passed the check before a concurrent change can still carry the
   * previously selected instance into the handshake; if that acceptor is deactivated before acceptance, the accept call can fail.
   * <p>
   * The returned handler wraps the context's current handler, preserving the existing application handler as its fallback. Install it as
   * the context's handler before starting the server. Use a path spec such as {@code /net4j/*}, because Net4j clients append the selected
   * acceptor name to the path. The request's secure state must match the acceptor type ({@code ws} for non-secure, {@code wss} for
   * secure requests); TLS is configured by the application's server connectors. The explicit acceptor set is snapshotted for this
   * handler, but registration and deactivation remain lifecycle responsibilities of the caller and the Net4j container.
   *
   * @param server the operator-owned Jetty server used to create the upgrade handler
   * @param context the context that will contain the WebSocket mapping
   * @param pathSpec the Jetty path specification for the Net4j WebSocket endpoint
   * @param acceptors the active acceptors explicitly exposed by this endpoint; each must be the currently registered instance
   * @return the configured upgrade handler to link into the Jetty handler tree
   * @throws IllegalArgumentException if an argument is {@code null}, the path is empty, or the acceptors are empty, inactive, or duplicated
   */
  public static WebSocketUpgradeHandler createUpgradeHandler(Server server, ContextHandler context, String pathSpec, IWSAcceptor... acceptors)
  {
    if (server == null //
        || context == null //
        || pathSpec == null //
        || pathSpec.isEmpty() //
        || acceptors == null //
        || acceptors.length == 0)
    {
      throw new IllegalArgumentException("server, context, pathSpec, and at least one acceptor are required"); //$NON-NLS-1$
    }

    Map<String, IWSAcceptor> scopedAcceptors = new HashMap<>();

    for (IWSAcceptor acceptor : acceptors)
    {
      if (acceptor == null //
          || IWSAcceptorManager.INSTANCE.getAcceptor(acceptor.getName()) != acceptor //
          || scopedAcceptors.put(acceptor.getName(), acceptor) != null)
      {
        throw new IllegalArgumentException("acceptors must be active and have distinct names"); //$NON-NLS-1$
      }
    }

    Map<String, IWSAcceptor> acceptorScope = Map.copyOf(scopedAcceptors);
    Handler existingHandler = context.getHandler();

    WebSocketUpgradeHandler upgradeHandler = WebSocketUpgradeHandler.from(server, context,
        (ServerWebSocketContainer container) -> container.addMapping(pathSpec, (request, response, callback) -> {
          String acceptorName = request.getHeaders().get(IWSConnector.ACCEPTOR_HEADER_NAME);
          IWSAcceptor acceptor = acceptorScope.get(acceptorName);
          String decodedPath = request.getHttpURI().getDecodedPath();
          if (acceptor == null //
              || decodedPath == null //
              || !decodedPath.endsWith("/" + IWSConnector.ACCEPTOR_NAME_PREFIX + acceptor.getName()) //$NON-NLS-1$
              || IWSAcceptorManager.INSTANCE.getAcceptor(acceptor.getName()) != acceptor)
          {
            response.setStatus(404);
            callback.succeeded();
            return null;
          }

          boolean expectedSecure = WSSUtil.FACTORY_TYPE.equals(acceptor.getType());

          if (request.isSecure() != expectedSecure || !WSUtil.FACTORY_TYPE.equals(acceptor.getType()) && !expectedSecure)
          {
            response.setStatus(403);
            callback.succeeded();
            return null;
          }

          return new JettyWebSocketMessageConnection(acceptor);
        }));

    upgradeHandler.setHandler(existingHandler);
    return upgradeHandler;
  }
}
