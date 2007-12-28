/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public interface ITCPSelectorListener
{
  public void handleRegistration(SelectionKey selectionKey);

  /**
   * @author Eike Stepper
   */
  public interface Passive extends ITCPSelectorListener
  {
    public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel);
  }

  /**
   * @author Eike Stepper
   */
  public interface Active extends ITCPSelectorListener
  {
    public void handleConnect(ITCPSelector selector, SocketChannel channel);

    public void handleRead(ITCPSelector selector, SocketChannel socketChannel);

    public void handleWrite(ITCPSelector selector, SocketChannel socketChannel);
  }
}