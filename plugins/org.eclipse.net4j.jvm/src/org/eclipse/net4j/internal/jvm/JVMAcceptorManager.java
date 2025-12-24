/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.IJVMAcceptorManager;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 */
public class JVMAcceptorManager implements IJVMAcceptorManager
{
  // @Singleton
  public static final JVMAcceptorManager INSTANCE = new JVMAcceptorManager();

  private IRegistry<String, IJVMAcceptor> acceptorRegistry = new HashMapRegistry<>();

  @Override
  public IRegistry<String, IJVMAcceptor> getAcceptorRegistry()
  {
    // TODO Introduce UnmodifiableRegistry
    return acceptorRegistry;
  }

  @Override
  public JVMAcceptor getAcceptor(String name)
  {
    return (JVMAcceptor)acceptorRegistry.get(name);
  }

  public boolean registerAcceptor(JVMAcceptor acceptor)
  {
    String name = acceptor.getName();
    if (!acceptorRegistry.containsKey(name))
    {
      acceptorRegistry.put(name, acceptor);
      return true;
    }

    return false;
  }

  public boolean deregisterAcceptor(JVMAcceptor acceptor)
  {
    return acceptorRegistry.remove(acceptor.getName()) != null;
  }
}
