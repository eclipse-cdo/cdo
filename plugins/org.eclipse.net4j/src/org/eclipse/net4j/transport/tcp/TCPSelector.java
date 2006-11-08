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
package org.eclipse.net4j.transport.tcp;

import org.eclipse.net4j.transport.tcp.TCPSelectorListener.Active;
import org.eclipse.net4j.transport.tcp.TCPSelectorListener.Passive;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public interface TCPSelector
{
  public void invokeAsync(Runnable operation);

  public void registerAsync(ServerSocketChannel channel, Passive listener);

  public void registerAsync(SocketChannel channel, Active listener);

  public boolean invoke(Runnable operation, long timeout);

  public SelectionKey register(ServerSocketChannel channel, Passive listener, long timeout);

  public SelectionKey register(SocketChannel channel, Active listener, long timeout);

  public void setConnectInterest(SelectionKey selectionKey, boolean on);

  public void setReadInterest(SelectionKey selectionKey, boolean on);

  public void setWriteInterest(SelectionKey selectionKey, boolean on);
}
