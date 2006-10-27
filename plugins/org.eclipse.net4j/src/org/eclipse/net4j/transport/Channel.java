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

import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryElement;

/**
 * A bidirectional communications channel for the asynchronous exchange of
 * {@link Buffer}s. A channel is lightweight and virtual in the sense that it
 * does not necessarily represent a single physical connection like a TCP socket
 * connection. The underlying physical connection is represented by a
 * {@link Connector}.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface Channel extends BufferHandler, IRegistryElement<ChannelID>
{
  public static final IRegistry<ChannelID, Channel> REGISTRY = new HashMapRegistry();

  public short getChannelIndex();

  public Connector getConnector();

  public void sendBuffer(Buffer buffer);

  public BufferHandler getReceiveHandler();

  public void setReceiveHandler(BufferHandler receiveHandler);

  public void close();
}
