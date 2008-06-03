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
package org.eclipse.net4j.internal.tcp;

import org.eclipse.net4j.internal.tcp.bundle.OM;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;

/**
 * @author Eike Stepper
 */
public final class SelectorUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, SelectorUtil.class);

  private SelectorUtil()
  {
  }

  public static String formatInterestOps(int newOps)
  {
    StringBuilder builder = new StringBuilder();
    if ((newOps & SelectionKey.OP_ACCEPT) != 0)
    {
      addInterestOp(builder, "ACCEPT"); //$NON-NLS-1$
    }

    if ((newOps & SelectionKey.OP_CONNECT) != 0)
    {
      addInterestOp(builder, "CONNECT"); //$NON-NLS-1$
    }

    if ((newOps & SelectionKey.OP_READ) != 0)
    {
      addInterestOp(builder, "READ"); //$NON-NLS-1$
    }

    if ((newOps & SelectionKey.OP_WRITE) != 0)
    {
      addInterestOp(builder, "WRITE"); //$NON-NLS-1$
    }

    return builder.toString();
  }

  public static void setInterest(SelectionKey selectionKey, int operation, boolean interested)
  {
    if (selectionKey == null || !selectionKey.isValid())
    {
      return;
    }

    try
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
        if (TRACER.isEnabled())
        {
          TRACER.trace("Setting interest " //$NON-NLS-1$
              + formatInterestOps(newOps)
              + (oldOps == 0 ? "" : " (was " + formatInterestOps(oldOps).toLowerCase() + ")")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        selectionKey.interestOps(newOps);
      }
    }
    catch (CancelledKeyException ignore)
    {
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
      builder.append("|"); //$NON-NLS-1$
    }

    builder.append(op);
  }
}
