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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.Connector.Type;

/**
 * @author Eike Stepper
 */
public abstract class AbstractProtocolFactory implements ProtocolFactory
{
  public AbstractProtocolFactory()
  {
  }

  public final boolean isForClients()
  {
    return getConnectorTypes().contains(Type.CLIENT);
  }

  public final boolean isForServers()
  {
    return getConnectorTypes().contains(Type.SERVER);
  }

  public final boolean isSymmetric()
  {
    return isForClients() && isForServers();
  }
}
