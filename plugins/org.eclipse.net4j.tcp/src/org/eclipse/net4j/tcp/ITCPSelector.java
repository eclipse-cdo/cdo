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
package org.eclipse.net4j.tcp;

import org.eclipse.net4j.tcp.ITCPSelectorListener.Active;
import org.eclipse.net4j.tcp.ITCPSelectorListener.Passive;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public interface ITCPSelector
{
  public void invokeAsync(Runnable operation);

  public void registerAsync(ServerSocketChannel channel, Passive listener);

  public void registerAsync(SocketChannel channel, Active listener);

  public void setConnectInterest(SelectionKey selectionKey, boolean on);

  public void setReadInterest(SelectionKey selectionKey, boolean on);

  public void setWriteInterest(SelectionKey selectionKey, boolean on);
}
