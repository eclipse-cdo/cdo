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

/**
 * A bidirectional communications channel for the asynchronous exchange of
 * {@link IBuffer}s. A channel is lightweight and virtual in the sense that it
 * does not necessarily represent a single physical connection like a TCP socket
 * connection. The underlying physical connection is represented by a
 * {@link IConnector}.
 * <p>
 * This interface is <b>not</b> intended to be implemented by clients.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface IChannel extends IBufferHandler
{
  public IChannelID getID();

  public short getChannelIndex();

  public IConnector getConnector();

  public void sendBuffer(IBuffer buffer);

  public IBufferHandler getReceiveHandler();

  public void setReceiveHandler(IBufferHandler receiveHandler);

  public void close();
}
