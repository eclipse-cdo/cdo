/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
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

  private IRegistry<String, IJVMAcceptor> acceptorRegistry = new HashMapRegistry<String, IJVMAcceptor>();

  public IRegistry<String, IJVMAcceptor> getAcceptorRegistry()
  {
    // TODO Introduce UnmodifiableRegistry
    return acceptorRegistry;
  }

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
