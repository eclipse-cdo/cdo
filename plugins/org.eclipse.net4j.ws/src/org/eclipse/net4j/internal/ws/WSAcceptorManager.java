/*
 * Copyright (c) 2020, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSAcceptorManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Maintains the optional shared registry of WebSocket acceptors for integrations that route by name through this manager.
 * <p>
 * Manager registration rejects an already registered name, and registration and identity-checked deregistration are
 * atomic with respect to this manager's operations. The exposed registry is mutable, so its contents are not guaranteed
 * to contain only active acceptors. Integrations may instead route through explicitly supplied acceptors. Provider
 * instances and accepted message connections remain owned by their respective containers and connectors.
 *
 * @author Eike Stepper
 */
public class WSAcceptorManager implements IWSAcceptorManager
{
  public static final WSAcceptorManager INSTANCE = new WSAcceptorManager();

  /**
   * Shared name-to-acceptor routing table exposed for integrations that use manager-based routing.
   */
  private final IRegistry<String, IWSAcceptor> acceptorRegistry = new ConcurrentWSAcceptorRegistry();

  public WSAcceptorManager()
  {
  }

  @Override
  public IRegistry<String, IWSAcceptor> getAcceptorRegistry()
  {
    return acceptorRegistry;
  }

  @Override
  public IWSAcceptor getAcceptor(String name)
  {
    return acceptorRegistry.get(name);
  }

  public boolean registerAcceptor(IWSAcceptor acceptor)
  {
    String name = acceptor.getName();

    synchronized (acceptorRegistry)
    {
      if (acceptorRegistry.containsKey(name))
      {
        return false;
      }

      acceptorRegistry.put(name, acceptor);
      return true;
    }
  }

  public boolean deregisterAcceptor(IWSAcceptor acceptor)
  {
    String name = acceptor.getName();

    synchronized (acceptorRegistry)
    {
      if (acceptorRegistry.get(name) == acceptor)
      {
        acceptorRegistry.remove(name);
        return true;
      }

      return false;
    }
  }

  /**
   * Supplies a concurrent backing map for the publicly exposed acceptor registry.
   *
   * @author Eike Stepper
   */
  private static final class ConcurrentWSAcceptorRegistry extends HashMapRegistry<String, IWSAcceptor>
  {
    private final Map<String, IWSAcceptor> map = new ConcurrentHashMap<>();

    public ConcurrentWSAcceptorRegistry()
    {
    }

    @Override
    protected Map<String, IWSAcceptor> getMap()
    {
      return map;
    }
  }
}
