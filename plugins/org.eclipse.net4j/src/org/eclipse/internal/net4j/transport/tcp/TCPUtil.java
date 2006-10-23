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
package org.eclipse.internal.net4j.transport.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Eike Stepper
 */
public final class TCPUtil
{
  private TCPUtil()
  {
  }

  public static String toString(ServerSocketChannel channel)
  {
    return channel.toString();
  }

  public static String toString(SocketChannel channel)
  {
    return channel.toString();
  }

  public static String formatInterestOps(int newOps)
  {
    StringBuilder builder = new StringBuilder();
    if ((newOps & SelectionKey.OP_ACCEPT) != 0)
    {
      addInterestOp(builder, "ACCEPT");
    }

    if ((newOps & SelectionKey.OP_CONNECT) != 0)
    {
      addInterestOp(builder, "CONNECT");
    }

    if ((newOps & SelectionKey.OP_READ) != 0)
    {
      addInterestOp(builder, "READ");
    }

    if ((newOps & SelectionKey.OP_WRITE) != 0)
    {
      addInterestOp(builder, "WRITE");
    }

    return builder.toString();
  }

  public static void setInterest(SelectionKey selectionKey, int operation, boolean interested)
  {
    int newOps;
    int oldOps = selectionKey.interestOps();
    if (interested)
    {
      newOps = oldOps | operation;
    }
    else
    {
      newOps = oldOps & ~operation;
    }

    if (oldOps != newOps)
    {
      System.out.println(selectionKey.channel().toString() + ": Setting interest "
          + formatInterestOps(newOps) + " (was " + formatInterestOps(oldOps).toLowerCase() + ")");
      selectionKey.interestOps(newOps);
    }
  }

  public static void setAcceptInterest(SelectionKey selectionKey, boolean interested)
  {
    setInterest(selectionKey, SelectionKey.OP_ACCEPT, interested);
  }

  public static void setConnectInterest(SelectionKey selectionKey, boolean interested)
  {
    setInterest(selectionKey, SelectionKey.OP_CONNECT, interested);
  }

  public static void setReadInterest(SelectionKey selectionKey, boolean interested)
  {
    setInterest(selectionKey, SelectionKey.OP_READ, interested);
  }

  public static void setWriteInterest(SelectionKey selectionKey, boolean interested)
  {
    setInterest(selectionKey, SelectionKey.OP_WRITE, interested);
  }

  private static void addInterestOp(StringBuilder builder, String op)
  {
    if (builder.length() != 0)
    {
      builder.append("|");
    }

    builder.append(op);
  }
}
