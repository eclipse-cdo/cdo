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

import org.eclipse.net4j.util.registry.IRegistry;

/**
 * @author Eike Stepper
 */
public interface TransportContainer
{
  public BufferPool getBufferPool();

  public IRegistry<ProtocolFactoryID, ProtocolFactory> getProtocolFactoryRegistry();

  public IRegistry<Integer, Connector> getConnectorRegistry();

  public IRegistry<ChannelID, Channel> getChannelRegistry();
}
