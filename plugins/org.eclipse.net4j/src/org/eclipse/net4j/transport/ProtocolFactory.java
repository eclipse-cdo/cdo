/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.transport;

import org.eclipse.net4j.transport.Connector.Type;
import org.eclipse.net4j.util.registry.IRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface ProtocolFactory
{
  public static final IRegistry<String, ProtocolFactory> CLIENT_REGISTRY = new org.eclipse.internal.net4j.transport.ClientProtocolFactoryRegistry();

  public static final IRegistry<String, ProtocolFactory> SERVER_REGISTRY = new org.eclipse.internal.net4j.transport.ServerProtocolFactoryRegistry();

  public static final Set<Type> FOR_CLIENTS = Collections.singleton(Type.CLIENT);

  public static final Set<Type> FOR_SERVERS = Collections.singleton(Type.SERVER);

  public static final Set<Type> SYMMETRIC = Collections.unmodifiableSet(new HashSet(Arrays
      .asList(new Type[] { Type.CLIENT, Type.SERVER })));

  public String getProtocolID();

  public Set<Type> getConnectorTypes();

  public boolean isForClients();

  public boolean isForServers();

  public boolean isSymmetric();

  public Protocol createProtocol(Channel channel, Object protocolData);
}