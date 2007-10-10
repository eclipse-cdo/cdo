/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
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
  public void orderRegistration(ServerSocketChannel channel, Passive listener);

  public void orderRegistration(SocketChannel channel, boolean client, Active listener);

  public void orderConnectInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderReadInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderWriteInterest(SelectionKey selectionKey, boolean client, boolean on);
}
