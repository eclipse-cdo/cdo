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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.IProtocolFactory;
import org.eclipse.net4j.transport.IProtocolFactoryID;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public abstract class ProtocolFactory implements IProtocolFactory
{
  public ProtocolFactory()
  {
  }

  public final boolean isForClients()
  {
    return getLocations().contains(ConnectorLocation.CLIENT);
  }

  public final boolean isForServers()
  {
    return getLocations().contains(ConnectorLocation.SERVER);
  }

  public final boolean isSymmetric()
  {
    return isForClients() && isForServers();
  }

  public IProtocolFactoryID getID(ConnectorLocation location)
  {
    return ProtocolFactoryID.create(location, getProtocolID());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Protocol.Factory({0}, {1})", getProtocolID(), getLocations());
  }
}
