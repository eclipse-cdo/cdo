/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITCPSelector
{
  public Selector getSocketSelector();

  public void orderRegistration(ServerSocketChannel channel, ITCPPassiveSelectorListener listener);

  public void orderRegistration(SocketChannel channel, boolean client, ITCPActiveSelectorListener listener);

  public void orderConnectInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderReadInterest(SelectionKey selectionKey, boolean client, boolean on);

  public void orderWriteInterest(SelectionKey selectionKey, boolean client, boolean on);
}
