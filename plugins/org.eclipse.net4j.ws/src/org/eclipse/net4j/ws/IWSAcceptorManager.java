/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
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
 * A singleton that manages all {@link IWSAcceptor WS acceptors} in the current JVM.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IWSAcceptorManager
{
  public static final IWSAcceptorManager INSTANCE = WSAcceptorManager.INSTANCE;

  public IRegistry<String, IWSAcceptor> getAcceptorRegistry();

  public IWSAcceptor getAcceptor(String name);
}
