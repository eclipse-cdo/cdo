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
package org.eclipse.net4j.transport;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public interface IProtocolFactory
{
  public static final Set<ConnectorLocation> FOR_CLIENTS = Collections.singleton(ConnectorLocation.CLIENT);

  public static final Set<ConnectorLocation> FOR_SERVERS = Collections.singleton(ConnectorLocation.SERVER);

  public static final Set<ConnectorLocation> SYMMETRIC = Collections.unmodifiableSet(new HashSet(Arrays
      .asList(new ConnectorLocation[] { ConnectorLocation.CLIENT, ConnectorLocation.SERVER })));

  public String getProtocolID();

  public Set<ConnectorLocation> getLocations();

  public boolean isForClients();

  public boolean isForServers();

  public boolean isSymmetric();

  public IProtocolFactoryID getID(ConnectorLocation location);

  public IProtocol createProtocol(IChannel channel, Object protocolData);
}