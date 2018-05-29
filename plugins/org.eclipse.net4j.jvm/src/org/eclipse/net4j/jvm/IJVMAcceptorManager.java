/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jvm;

import org.eclipse.net4j.internal.jvm.JVMAcceptorManager;
import org.eclipse.net4j.util.registry.IRegistry;

/**
 * A singleton that manages all {@link IJVMAcceptor JVM acceptors} in the current JVM.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IJVMAcceptorManager
{
  public static final IJVMAcceptorManager INSTANCE = JVMAcceptorManager.INSTANCE;

  public IRegistry<String, IJVMAcceptor> getAcceptorRegistry();

  public IJVMAcceptor getAcceptor(String name);
}
