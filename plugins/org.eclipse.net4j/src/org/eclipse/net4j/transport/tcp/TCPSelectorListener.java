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

import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public interface TCPSelectorListener
{
  /**
   * @author Eike Stepper
   */
  public interface Passive
  {
    public void handleAccept(TCPSelector selector, ServerSocketChannel serverSocketChannel);
  }

  /**
   * @author Eike Stepper
   */
  public interface Active
  {
    public void handleConnect(TCPSelector selector, SocketChannel channel);

    public void handleRead(TCPSelector selector, SocketChannel socketChannel);

    public void handleWrite(TCPSelector selector, SocketChannel socketChannel);
  }
}