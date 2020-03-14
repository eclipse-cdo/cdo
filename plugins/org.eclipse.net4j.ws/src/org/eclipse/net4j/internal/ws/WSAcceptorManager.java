/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.IWSAcceptorManager;

/**
 * @author Eike Stepper
 */
public class WSAcceptorManager implements IWSAcceptorManager
{
  // @Singleton
  public static final WSAcceptorManager INSTANCE = new WSAcceptorManager();

  private IRegistry<String, IWSAcceptor> acceptorRegistry = new HashMapRegistry<>();

  @Override
  public IRegistry<String, IWSAcceptor> getAcceptorRegistry()
  {
    return acceptorRegistry;
  }

  @Override
  public WSAcceptor getAcceptor(String name)
  {
    return (WSAcceptor)acceptorRegistry.get(name);
  }

  public boolean registerAcceptor(WSAcceptor acceptor)
  {
    String name = acceptor.getName();
    if (!acceptorRegistry.containsKey(name))
    {
      acceptorRegistry.put(name, acceptor);
      return true;
    }

    return false;
  }

  public boolean deregisterAcceptor(WSAcceptor acceptor)
  {
    return acceptorRegistry.remove(acceptor.getName()) != null;
  }
}
