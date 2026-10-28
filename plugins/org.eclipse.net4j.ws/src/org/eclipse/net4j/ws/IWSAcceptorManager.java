/*
 * Copyright (c) 2020, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.ws;

import org.eclipse.net4j.internal.ws.WSAcceptorManager;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * Provides an optional JVM-wide registry of {@link IWSAcceptor WebSocket acceptors} by route name.
 * <p>
 * A server integration may use this registry to find an acceptor after a WebSocket upgrade. Other integrations can
 * route through acceptors supplied directly to their upgrade handler, as the Jetty integration does. Registration
 * through the manager rejects a second acceptor with the same name; container ownership remains with the container
 * that activated each acceptor. The live registry is exposed for lookup and can also be mutated directly; direct
 * mutation bypasses the manager's registration checks and lifecycle bookkeeping.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSAcceptorManager
{
  public static final IWSAcceptorManager INSTANCE = WSAcceptorManager.INSTANCE;

  /**
   * Returns the live registry available to integrations that use shared route lookup. Its contents are not guaranteed
   * to represent only active acceptors because direct mutations bypass the manager's duplicate-name check and lifecycle
   * bookkeeping; callers should use this method for lookup and use acceptor activation to manage entries.
   *
   * @return the mutable name-to-acceptor registry
   */
  public IRegistry<String, IWSAcceptor> getAcceptorRegistry();

  /**
   * Looks up the acceptor registered under a route name. Direct registry mutations can make the result inactive.
   *
   * @param name acceptor route name
   * @return the registered acceptor, or {@code null} if no acceptor is registered under that name
   */
  public IWSAcceptor getAcceptor(String name);
}
