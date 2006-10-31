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

import org.eclipse.net4j.transport.tcp.TCPSelector;
import org.eclipse.net4j.transport.tcp.TCPSelectorListener;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class TCPSelectorImpl extends AbstractLifecycle implements TCPSelector, Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_SELECTOR,
      TCPSelectorImpl.class);

  private static final long SELECT_TIMEOUT = 2;

  private Selector selector;

  private Thread thread;

  public TCPSelectorImpl()
  {
  }

  public SelectionKey register(ServerSocketChannel channel, TCPSelectorListener.Passive listener)
      throws ClosedChannelException
  {
    if (listener == null)
    {
      throw new IllegalArgumentException("listener == null"); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Registering " + TCPUtil.toString(channel)); //$NON-NLS-1$
    }

    return channel.register(selector, SelectionKey.OP_ACCEPT, listener);
  }

  public SelectionKey register(SocketChannel channel, TCPSelectorListener.Active listener)
      throws ClosedChannelException
  {
    if (listener == null)
    {
      throw new IllegalArgumentException("listener == null"); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace(this, "Registering " + TCPUtil.toString(channel)); //$NON-NLS-1$
    }

    return channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ, listener);
  }

  public void run()
  {
    while (isActive())
    {
      if (Thread.interrupted())
      {
        deactivate();
        break;
      }

      try
      {
        if (selector.select(SELECT_TIMEOUT) > 0)
        {
          Iterator<SelectionKey> it = selector.selectedKeys().iterator();
          while (it.hasNext())
          {
            SelectionKey selKey = it.next();
            it.remove();

            try
            {
              handleSelection(selKey);
            }
            catch (CancelledKeyException ignore)
            {
              ; // Do nothing
            }
            catch (Exception ex)
            {
              Net4j.LOG.error(ex);
              selKey.cancel();
            }
          }
        }
      }
      catch (ClosedSelectorException ex)
      {
        break;
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
        deactivate();
        break;
      }
    }
  }

  @Override
  public String toString()
  {
    return "TCPSelector"; //$NON-NLS-1$
  }

  protected void handleSelection(SelectionKey selKey) throws IOException
  {
    SelectableChannel channel = selKey.channel();
    if (channel instanceof ServerSocketChannel)
    {
      ServerSocketChannel ssChannel = (ServerSocketChannel)selKey.channel();
      TCPSelectorListener.Passive listener = (TCPSelectorListener.Passive)selKey.attachment();

      if (selKey.isAcceptable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Accepting " + TCPUtil.toString(ssChannel)); //$NON-NLS-1$
        }

        listener.handleAccept(this, ssChannel);
      }
    }
    else if (channel instanceof SocketChannel)
    {
      SocketChannel sChannel = (SocketChannel)channel;
      TCPSelectorListener.Active listener = (TCPSelectorListener.Active)selKey.attachment();

      if (selKey.isConnectable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Connecting " + TCPUtil.toString(sChannel)); //$NON-NLS-1$
        }

        listener.handleConnect(this, sChannel);
      }

      if (selKey.isReadable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Reading " + TCPUtil.toString(sChannel)); //$NON-NLS-1$
        }

        listener.handleRead(this, sChannel);
      }

      if (selKey.isWritable())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(this, "Writing " + TCPUtil.toString(sChannel)); //$NON-NLS-1$
        }

        listener.handleWrite(this, sChannel);
      }
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    selector = Selector.open();
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    Exception exception = null;

    try
    {
      thread.join(2 * SELECT_TIMEOUT + 200);
    }
    catch (RuntimeException ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      thread = null;
    }

    try
    {
      selector.close();
    }
    catch (Exception ex)
    {
      if (exception == null)
      {
        exception = ex;
      }
    }
    finally
    {
      selector = null;
    }

    if (exception != null)
    {
      throw exception;
    }
  }
}
