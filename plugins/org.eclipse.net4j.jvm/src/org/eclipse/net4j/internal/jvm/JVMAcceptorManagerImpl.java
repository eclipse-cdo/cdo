/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.jvm;

import org.eclipse.net4j.jvm.JVMAcceptor;
import org.eclipse.net4j.jvm.JVMAcceptorManager;
import org.eclipse.net4j.util.lifecycle.Singleton;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

/**
 * @author Eike Stepper
 */
public class JVMAcceptorManagerImpl implements JVMAcceptorManager
{
  @Singleton
  public static final JVMAcceptorManagerImpl INSTANCE = new JVMAcceptorManagerImpl();

  private IRegistry<String, JVMAcceptor> acceptorRegistry = new HashMapRegistry();

  public IRegistry<String, JVMAcceptor> getAcceptorRegistry()
  {
    // TODO Introduce UnmodifiableRegistry
    return acceptorRegistry;
  }

  public JVMAcceptorImpl getAcceptor(String name)
  {
    return (JVMAcceptorImpl)acceptorRegistry.get(name);
  }

  public boolean registerAcceptor(JVMAcceptorImpl acceptor)
  {
    String name = acceptor.getName();
    if (!acceptorRegistry.containsKey(name))
    {
      acceptorRegistry.put(name, acceptor);
      return true;
    }

    return false;
  }

  public boolean deregisterAcceptor(JVMAcceptorImpl acceptor)
  {
    return acceptorRegistry.remove(acceptor.getName()) != null;
  }
}
